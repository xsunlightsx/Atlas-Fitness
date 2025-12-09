package com.example.ATLAS.FITNESS.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "membresia")
public class Membresia {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "membresia_id")
    private Long idMembresia;
    
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
    
    @Column(name = "codigo_membresia", unique = true, nullable = false, length = 20)
    private String codigoMembresia;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TipoMembresia tipo = TipoMembresia.MENSUAL;
    
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;
    
    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Estado estado = Estado.ACTIVA;
    
    @Column(name = "dias_asistencia")
    private Integer diasAsistencia = 0;
    
    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro = LocalDateTime.now();
    
    @Column(name = "fecha_renovacion")
    private LocalDateTime fechaRenovacion;
    
    private String observaciones;
    
    // Enums
    public enum TipoMembresia {
        MENSUAL, TRIMESTRAL, SEMESTRAL, ANUAL
    }
    
    public enum Estado {
        ACTIVA, VENCIDA, SUSPENDIDA, CANCELADA
    }
    
    // Constructores
    public Membresia() {}
    
    public Membresia(Cliente cliente, TipoMembresia tipo, LocalDate fechaInicio, BigDecimal precio) {
        this.cliente = cliente;
        this.tipo = tipo;
        this.fechaInicio = fechaInicio;
        this.precio = precio;
        this.codigoMembresia = generarCodigoMembresia(cliente);
        calcularFechaFin(); // Llama al método privado
    }
    
    // Getters y Setters
    public Long getIdMembresia() { return idMembresia; }
    public void setIdMembresia(Long idMembresia) { this.idMembresia = idMembresia; }
    
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    
    public String getCodigoMembresia() { return codigoMembresia; }
    public void setCodigoMembresia(String codigoMembresia) { this.codigoMembresia = codigoMembresia; }
    
    public TipoMembresia getTipo() { return tipo; }
    public void setTipo(TipoMembresia tipo) { 
        this.tipo = tipo;
        calcularFechaFin(); // Recalcula al cambiar tipo
    }
    
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { 
        this.fechaInicio = fechaInicio;
        calcularFechaFin(); // Recalcula al cambiar fecha inicio
    }
    
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
    
    public Integer getDiasAsistencia() { return diasAsistencia; }
    public void setDiasAsistencia(Integer diasAsistencia) { this.diasAsistencia = diasAsistencia; }
    
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    
    public LocalDateTime getFechaRenovacion() { return fechaRenovacion; }
    public void setFechaRenovacion(LocalDateTime fechaRenovacion) { this.fechaRenovacion = fechaRenovacion; }
    
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    
    // Métodos auxiliares PRIVADO (solo para uso interno de la clase)
    private void calcularFechaFin() {
        if (fechaInicio != null && tipo != null) {
            this.fechaFin = calcularFechaFin(tipo, fechaInicio);
        }
    }
    
    // Método PÚBLICO y ESTÁTICO para calcular fecha fin
    public static LocalDate calcularFechaFin(TipoMembresia tipo, LocalDate fechaInicio) {
        if (fechaInicio == null || tipo == null) {
            return null;
        }
        
        return switch (tipo) {
            case MENSUAL -> fechaInicio.plusMonths(1);
            case TRIMESTRAL -> fechaInicio.plusMonths(3);
            case SEMESTRAL -> fechaInicio.plusMonths(6);
            case ANUAL -> fechaInicio.plusYears(1);
        };
    }
    
    private String generarCodigoMembresia(Cliente cliente) {
        return "MEM-" + cliente.getDni() + "-" + System.currentTimeMillis();
    }
    
    public boolean estaVencida() {
        return estado == Estado.VENCIDA || 
               (fechaFin != null && fechaFin.isBefore(LocalDate.now()));
    }
    
    public boolean estaActiva() {
        return estado == Estado.ACTIVA && 
               fechaFin != null && 
               !fechaFin.isBefore(LocalDate.now());
    }
    
    public void incrementarAsistencia() {
        this.diasAsistencia = (diasAsistencia == null) ? 1 : diasAsistencia + 1;
    }
    
    public String getTipoFormateado() {
        return switch (tipo) {
            case MENSUAL -> "Membresía Mensual";
            case TRIMESTRAL -> "Membresía Trimestral";
            case SEMESTRAL -> "Membresía Semestral";
            case ANUAL -> "Membresía Anual";
        };
    }
}