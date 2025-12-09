package com.example.ATLAS.FITNESS.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Usuario")
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Long usuarioId;
    
    @Column(name = "cliente_id", insertable = false, updatable = false)
    private Long clienteId;
    
    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;
    
    @Column(name = "password_hash", nullable = false, length = 255)
    private String password;
    
    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;
    
    @Column(name = "rol", nullable = false, length = 20)
    private String rol;
    
    @Column(name = "ultimo_login")
    private LocalDateTime ultimoLogin;
    
    @Column(name = "estado", length = 20)
    private String estado = "ACTIVO";
    
    // Campos para reset de contraseña
    @Column(name = "reset_token", length = 255)
    private String resetToken;
    
    @Column(name = "token_expira")
    private LocalDateTime tokenExpira;
    
    // RELACIÓN ELIMINADA: No hay relación bidireccional para evitar error
    // @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private Cliente cliente;
    
    // En su lugar, uso @Transient
    @Transient
    private Cliente cliente;
    
    // Enums como constantes (para evitar conflictos)
    public static final class Rol {
        public static final String ADMIN = "ADMIN";
        public static final String RECEPCIONISTA = "RECEPCIONISTA";
        public static final String ENTRENADOR = "ENTRENADOR";
        public static final String CLIENTE = "CLIENTE";
    }
    
    public static final class Estado {
        public static final String ACTIVO = "ACTIVO";
        public static final String INACTIVO = "INACTIVO";
        public static final String BLOQUEADO = "BLOQUEADO";
    }
    
    // Constructores
    public Usuario() {}
    
    public Usuario(String username, String password, String email, String rol) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.rol = rol;
    }
    
    // Getters y Setters
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    
    public LocalDateTime getUltimoLogin() { return ultimoLogin; }
    public void setUltimoLogin(LocalDateTime ultimoLogin) { this.ultimoLogin = ultimoLogin; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public String getResetToken() { return resetToken; }
    public void setResetToken(String resetToken) { this.resetToken = resetToken; }
    
    public LocalDateTime getTokenExpira() { return tokenExpira; }
    public void setTokenExpira(LocalDateTime tokenExpira) { this.tokenExpira = tokenExpira; }
    
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    
    // Métodos utilitarios
    public boolean estaActivo() {
        return Estado.ACTIVO.equals(estado);
    }
    
    public boolean esAdmin() {
        return Rol.ADMIN.equals(rol);
    }
    
    public boolean tokenValido() {
        return resetToken != null && 
               tokenExpira != null && 
               tokenExpira.isAfter(LocalDateTime.now());
    }
    
    @Override
    public String toString() {
        return "Usuario{" +
                "usuarioId=" + usuarioId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", rol='" + rol + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}