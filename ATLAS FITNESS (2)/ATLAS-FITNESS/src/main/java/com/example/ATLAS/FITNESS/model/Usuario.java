package com.example.ATLAS.FITNESS.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;
    
    @Column(unique = true, nullable = false, length = 50)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(unique = true, nullable = false, length = 100)
    private String email;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Rol rol = Rol.CLIENTE;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Estado estado = Estado.ACTIVO;
    
    private LocalDateTime fechaRegistro = LocalDateTime.now();
    private LocalDateTime ultimoLogin;
    private Integer intentosLogin = 0;
    private String resetToken;
    private LocalDateTime tokenExpira;
    
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    private Cliente cliente;
    
    // Enums
    public enum Rol {
        ADMIN, RECEPCIONISTA, ENTRENADOR, CLIENTE
    }
    
    public enum Estado {
        ACTIVO, INACTIVO, BLOQUEADO
    }
    
    // Constructores
    public Usuario() {}
    
    public Usuario(String username, String password, String email, Rol rol) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.rol = rol;
    }
    
    // Getters y Setters
    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
    
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
    
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    
    public LocalDateTime getUltimoLogin() { return ultimoLogin; }
    public void setUltimoLogin(LocalDateTime ultimoLogin) { this.ultimoLogin = ultimoLogin; }
    
    public Integer getIntentosLogin() { return intentosLogin; }
    public void setIntentosLogin(Integer intentosLogin) { this.intentosLogin = intentosLogin; }
    
    public String getResetToken() { return resetToken; }
    public void setResetToken(String resetToken) { this.resetToken = resetToken; }
    
    public LocalDateTime getTokenExpira() { return tokenExpira; }
    public void setTokenExpira(LocalDateTime tokenExpira) { this.tokenExpira = tokenExpira; }
    
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
}