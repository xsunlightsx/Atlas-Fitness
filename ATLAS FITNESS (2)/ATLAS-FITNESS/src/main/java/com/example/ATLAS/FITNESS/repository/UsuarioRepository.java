package com.example.ATLAS.FITNESS.repository;

import com.example.ATLAS.FITNESS.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByUsername(String username);
    
    Optional<Usuario> findByEmail(String email);
    
    Optional<Usuario> findByResetToken(String resetToken);
    
    Optional<Usuario> findByUsernameAndEstado(String username, String estado);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    // CONSULTA CORREGIDA: Ya no hay JOIN FETCH con cliente
    @Query("SELECT u FROM Usuario u WHERE u.usuarioId = :id")
    Optional<Usuario> findByIdWithCliente(@Param("id") Long id);
    
    @Query("SELECT u FROM Usuario u WHERE u.username = :username")
    Optional<Usuario> findByUsernameWithCliente(@Param("username") String username);
    
    @Modifying
    @Query("UPDATE Usuario u SET u.ultimoLogin = :fecha WHERE u.username = :username")
    void actualizarUltimoLogin(@Param("username") String username, 
                               @Param("fecha") LocalDateTime fecha);
    
    @Modifying
    @Query("UPDATE Usuario u SET u.estado = :estado WHERE u.username = :username")
    void cambiarEstado(@Param("username") String username, 
                      @Param("estado") String estado);
    
    @Modifying
    @Query("UPDATE Usuario u SET u.resetToken = :token, u.tokenExpira = :expira WHERE u.email = :email")
    void actualizarTokenReset(@Param("email") String email,
                             @Param("token") String token,
                             @Param("expira") LocalDateTime expira);
    
    @Modifying
    @Query("UPDATE Usuario u SET u.password = :password, u.resetToken = NULL, u.tokenExpira = NULL WHERE u.resetToken = :token")
    void resetPassword(@Param("token") String token,
                      @Param("password") String password);
    
    // Buscar usuario por ID de cliente
    Optional<Usuario> findByClienteId(Long clienteId);
}