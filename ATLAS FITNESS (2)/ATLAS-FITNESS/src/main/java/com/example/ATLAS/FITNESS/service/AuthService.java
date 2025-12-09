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
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {
    
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    
    public AuthService(UsuarioRepository usuarioRepository,
                      ClienteRepository clienteRepository,
                      PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Transactional
    public Usuario autenticar(LoginDTO loginDTO) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(loginDTO.getUsername());
        
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }
        
        Usuario usuario = usuarioOpt.get();
        
        // Verificar estado
        if (!usuario.estaActivo()) {
            throw new RuntimeException("Usuario inactivo o bloqueado");
        }
        
        // Verificar contraseña
        if (!passwordEncoder.matches(loginDTO.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }
        
        // Cargar el cliente asociado si existe
        if (usuario.getClienteId() != null) {
            Optional<Cliente> clienteOpt = clienteRepository.findById(usuario.getClienteId());
            clienteOpt.ifPresent(usuario::setCliente);
        }
        
        // Actualizar último login
        usuario.setUltimoLogin(LocalDateTime.now());
        usuarioRepository.save(usuario);
        
        return usuario;
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
        
        // 1. Crear el cliente
        Cliente cliente = new Cliente();
        cliente.setDni(registroDTO.getDni());
        cliente.setNombre(registroDTO.getNombre());
        cliente.setApellido(registroDTO.getApellido());
        cliente.setTelefono(registroDTO.getTelefono());
        cliente.setEmail(registroDTO.getEmail());
        cliente.setDireccion(registroDTO.getDireccion());
        cliente.setEstado("ACTIVO");
        
        Cliente clienteGuardado = clienteRepository.save(cliente);
        
        // 2. Crear usuario vinculado
        Usuario usuario = new Usuario();
        usuario.setUsername(registroDTO.getUsername());
        usuario.setPassword(passwordEncoder.encode(registroDTO.getPassword()));
        usuario.setEmail(registroDTO.getEmail());
        usuario.setRol(Usuario.Rol.CLIENTE);
        usuario.setEstado(Usuario.Estado.ACTIVO);
        usuario.setClienteId(clienteGuardado.getClienteId());
        
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        
        // Asociar cliente al usuario (en memoria)
        usuarioGuardado.setCliente(clienteGuardado);
        
        return usuarioGuardado;
    }
    
    public void solicitarResetPassword(String email) {
        usuarioRepository.findByEmail(email).ifPresent(usuario -> {
            String token = UUID.randomUUID().toString();
            usuario.setResetToken(token);
            usuario.setTokenExpira(LocalDateTime.now().plusHours(24));
            usuarioRepository.save(usuario);
            
            // TODO: Implementar envío de email
            System.out.println("Token para " + email + ": " + token);
        });
    }
    
    @Transactional
    public void resetPassword(String token, String nuevaPassword) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByResetToken(token);
        
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Token inválido");
        }
        
        Usuario usuario = usuarioOpt.get();
        
        if (!usuario.tokenValido()) {
            throw new RuntimeException("Token expirado");
        }
        
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuario.setResetToken(null);
        usuario.setTokenExpira(null);
        usuarioRepository.save(usuario);
    }
    
    public boolean verificarCredenciales(String username, String password) {
        return usuarioRepository.findByUsername(username)
                .filter(usuario -> usuario.estaActivo())
                .filter(usuario -> passwordEncoder.matches(password, usuario.getPassword()))
                .isPresent();
    }
}