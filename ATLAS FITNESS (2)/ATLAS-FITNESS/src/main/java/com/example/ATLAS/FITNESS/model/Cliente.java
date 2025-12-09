package com.example.ATLAS.FITNESS.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Cliente")
public class Cliente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cliente_id")
    private Long clienteId;
    
    @Column(name = "dni", unique = true, nullable = false, length = 8)
    private String dni;
    
    @Column(name = "ruc", length = 11)
    private String ruc;
    
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
    
    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;
    
    @Column(name = "telefono", length = 15)
    private String telefono;
    
    @Column(name = "direccion", columnDefinition = "TEXT")
    private String direccion;
    
    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;
    
    @Column(name = "fecha_registro", insertable = false, updatable = false)
    private LocalDateTime fechaRegistro;
    
    @Column(name = "estado", length = 20)
    private String estado = "ACTIVO";
    
    // Campos adicionales para perfil fitness
    @Column(name = "fecha_nacimiento")
    private LocalDateTime fechaNacimiento;
    
    @Column(name = "genero", length = 20)
    private String genero;
    
    // CAMBIO IMPORTANTE: Cambiar Double por BigDecimal
    @Column(name = "altura", precision = 5, scale = 2)
    private BigDecimal altura;
    
    @Column(name = "peso", precision = 5, scale = 2)
    private BigDecimal peso;
    
    @Column(name = "objetivo", length = 50)
    private String objetivo;
    
    @Column(name = "fecha_ultima_visita")
    private LocalDateTime fechaUltimaVisita;
    
    // RELACIÓN ELIMINADA: No hay relación bidireccional con Usuario
    // @OneToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "cliente_id", referencedColumnName = "cliente_id", insertable = false, updatable = false)
    // private Usuario usuario;
    
    // En su lugar, uso @Transient
    @Transient
    private Usuario usuario;
    
    // Clase interna para Estado
    public static final class Estado {
        public static final String ACTIVO = "ACTIVO";
        public static final String INACTIVO = "INACTIVO";
        public static final String BLOQUEADO = "BLOQUEADO";
    }
    
    // Constructores
    public Cliente() {}
    
    public Cliente(String dni, String nombre, String apellido, String email) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.estado = Estado.ACTIVO;
    }
    
    // Getters y Setters
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    
    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    
    public LocalDateTime getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDateTime fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
    
    // Getters y Setters para BigDecimal
    public BigDecimal getAltura() { return altura; }
    public void setAltura(BigDecimal altura) { this.altura = altura; }
    
    public BigDecimal getPeso() { return peso; }
    public void setPeso(BigDecimal peso) { this.peso = peso; }
    
    // Métodos utilitarios para trabajar con Double (compatibilidad)
    public Double getAlturaAsDouble() {
        return altura != null ? altura.doubleValue() : null;
    }
    
    public void setAltura(Double altura) {
        this.altura = altura != null ? BigDecimal.valueOf(altura) : null;
    }
    
    public Double getPesoAsDouble() {
        return peso != null ? peso.doubleValue() : null;
    }
    
    public void setPeso(Double peso) {
        this.peso = peso != null ? BigDecimal.valueOf(peso) : null;
    }
    
    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }
    
    public LocalDateTime getFechaUltimaVisita() { return fechaUltimaVisita; }
    public void setFechaUltimaVisita(LocalDateTime fechaUltimaVisita) { this.fechaUltimaVisita = fechaUltimaVisita; }
    
    // Métodos utilitarios
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
    
    public boolean estaActivo() {
        return Estado.ACTIVO.equals(estado);
    }
    
    @Override
    public String toString() {
        return "Cliente{" +
                "clienteId=" + clienteId +
                ", dni='" + dni + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", email='" + email + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}