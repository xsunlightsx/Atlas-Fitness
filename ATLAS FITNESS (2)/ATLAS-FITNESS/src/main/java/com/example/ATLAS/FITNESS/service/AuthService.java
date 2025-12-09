package com.example.ATLAS.FITNESS.service;

import com.example.ATLAS.FITNESS.dto.LoginDTO;
import com.example.ATLAS.FITNESS.dto.RegistroClienteDTO;
import com.example.ATLAS.FITNESS.model.Cliente;
import com.example.ATLAS.FITNESS.model.Usuario;
import com.example.ATLAS.FITNESS.repository.ClienteRepository;
import com.example.ATLAS.FITNESS.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {
    
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    
    public AuthService(UsuarioRepository usuarioRepository,
                      ClienteRepository clienteRepository,
                      PasswordEncoder passwordEncoder,
                      EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }
    
    @Transactional
    public Usuario registrarCliente(RegistroClienteDTO registroDTO) {
        // Validar que el username no exista
        if (usuarioRepository.existsByUsername(registroDTO.getUsername())) {
            throw new RuntimeException("El username ya está registrado");
        }
        
        // Validar que el email no exista
        if (usuarioRepository.existsByEmail(registroDTO.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        
        // Validar que el DNI no exista
        if (clienteRepository.findByDni(registroDTO.getDni()).isPresent()) {
            throw new RuntimeException("El DNI ya está registrado");
        }
        
        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setUsername(registroDTO.getUsername());
        usuario.setPassword(passwordEncoder.encode(registroDTO.getPassword()));
        usuario.setEmail(registroDTO.getEmail());
        usuario.setRol(Usuario.Rol.CLIENTE);
        usuario.setEstado(Usuario.Estado.ACTIVO);
        
        // Crear cliente
        Cliente cliente = new Cliente();
        cliente.setDni(registroDTO.getDni());
        cliente.setNombre(registroDTO.getNombre());
        cliente.setApellido(registroDTO.getApellido());
        cliente.setTelefono(registroDTO.getTelefono());
        cliente.setEmail(registroDTO.getEmail());
        cliente.setDireccion(registroDTO.getDireccion());
        cliente.setUsuario(usuario);
        
        // Guardar en cascada
        usuario.setCliente(cliente);
        usuarioRepository.save(usuario);
        
        // Enviar email de bienvenida
        emailService.enviarEmailBienvenida(cliente.getEmail(), cliente.getNombre());
        
        return usuario;
    }
    
    public Usuario autenticar(LoginDTO loginDTO) {
        return usuarioRepository.findByUsername(loginDTO.getUsername())
                .filter(usuario -> passwordEncoder.matches(loginDTO.getPassword(), usuario.getPassword()))
                .filter(usuario -> usuario.getEstado() == Usuario.Estado.ACTIVO)
                .map(usuario -> {
                    usuario.setUltimoLogin(LocalDateTime.now());
                    usuario.setIntentosLogin(0);
                    return usuarioRepository.save(usuario);
                })
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));
    }
    
    public void solicitarResetPassword(String email) {
        usuarioRepository.findByEmail(email).ifPresent(usuario -> {
            String token = UUID.randomUUID().toString();
            usuario.setResetToken(token);
            usuario.setTokenExpira(LocalDateTime.now().plusHours(24));
            usuarioRepository.save(usuario);
            
            // Enviar email con token
            emailService.enviarEmailResetPassword(email, token);
        });
    }
    
    @Transactional
    public void resetPassword(String token, String nuevaPassword) {
        usuarioRepository.findByResetToken(token).ifPresent(usuario -> {
            if (usuario.getTokenExpira().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Token expirado");
            }
            
            usuario.setPassword(passwordEncoder.encode(nuevaPassword));
            usuario.setResetToken(null);
            usuario.setTokenExpira(null);
            usuarioRepository.save(usuario);
        });
    }
}