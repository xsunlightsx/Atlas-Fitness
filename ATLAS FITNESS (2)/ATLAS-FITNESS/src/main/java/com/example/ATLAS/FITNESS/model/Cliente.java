package com.example.ATLAS.FITNESS.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "cliente")
public class Cliente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCliente;
    
    @Column(unique = true, nullable = false, length = 8)
    private String dni;
    
    @Column(length = 11)
    private String ruc;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(nullable = false, length = 100)
    private String apellido;
    
    @Column(nullable = false, length = 15)
    private String telefono;
    
    @Column(length = 200)
    private String direccion;
    
    @Column(unique = true, length = 100)
    private String email;
    
    @Temporal(TemporalType.DATE)
    private Date fechaNacimiento;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Genero genero = Genero.OTRO;
    
    private Double peso;
    private Double altura;
    private String objetivo;
    
    @Column(nullable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();
    
    private LocalDateTime fechaUltimaVisita;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Estado estado = Estado.ACTIVO;
    
    @OneToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
    
    @OneToMany(mappedBy = "cliente")
    private List<Venta> compras;
    
    // Enums
    public enum Genero {
        MASCULINO, FEMENINO, OTRO
    }
    
    public enum Estado {
        ACTIVO, INACTIVO, MOROSO
    }
    
    // Constructores
    public Cliente() {}
    
    public Cliente(String dni, String nombre, String apellido, String telefono) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
    }
    
    // Getters y Setters
    public Long getIdCliente() { return idCliente; }
    public void setIdCliente(Long idCliente) { this.idCliente = idCliente; }
    
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
    
    public Date getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(Date fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    
    public Genero getGenero() { return genero; }
    public void setGenero(Genero genero) { this.genero = genero; }
    
    public Double getPeso() { return peso; }
    public void setPeso(Double peso) { this.peso = peso; }
    
    public Double getAltura() { return altura; }
    public void setAltura(Double altura) { this.altura = altura; }
    
    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }
    
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    
    public LocalDateTime getFechaUltimaVisita() { return fechaUltimaVisita; }
    public void setFechaUltimaVisita(LocalDateTime fechaUltimaVisita) { this.fechaUltimaVisita = fechaUltimaVisita; }
    
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
    
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    
    public List<Venta> getCompras() { return compras; }
    public void setCompras(List<Venta> compras) { this.compras = compras; }
    
    // Métodos auxiliares
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}