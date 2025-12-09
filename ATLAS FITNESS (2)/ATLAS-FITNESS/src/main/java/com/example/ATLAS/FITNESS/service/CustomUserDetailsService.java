package com.example.ATLAS.FITNESS.service;

import com.example.ATLAS.FITNESS.model.Usuario;
import com.example.ATLAS.FITNESS.repository.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UsuarioRepository usuarioRepository;
    
    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        // Buscar usuario en BD
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> 
                    new UsernameNotFoundException("Usuario no encontrado: " + username)
                );
        
        // Verificar estado
        if (!"ACTIVO".equals(usuario.getEstado())) {
            throw new UsernameNotFoundException("Usuario no está activo: " + username);
        }
        
        // Actualizar último login (en servicio separado)
        // usuarioRepository.actualizarUltimoLogin(username, LocalDateTime.now());
        
        // Crear authorities (roles)
        List<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toUpperCase())
        );
        
        // Retornar UserDetails con los datos de nuestro Usuario
        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())  // Ya está encriptado en BD
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!usuario.estaActivo())
                .build();
    }
    
    @Transactional
    public void registrarLoginExitoso(String username) {
        usuarioRepository.actualizarUltimoLogin(username, LocalDateTime.now());
    }
}