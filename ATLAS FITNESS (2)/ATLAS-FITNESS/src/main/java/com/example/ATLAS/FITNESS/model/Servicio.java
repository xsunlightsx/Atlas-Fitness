package com.example.ATLAS.FITNESS.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "servicio")
public class Servicio {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idServicio;
    
    @Column(unique = true, nullable = false, length = 20)
    private String codigo;
    
    @Column(nullable = false, length = 200)
    private String nombre;
    
    @Column(length = 1000)
    private String descripcion;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private TipoServicio tipo = TipoServicio.ENTRENAMIENTO_PERSONAL;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;
    
    @Column(name = "duracion_minutos")
    private Integer duracionMinutos = 60;
    
    @Column(name = "capacidad_maxima")
    private Integer capacidadMaxima = 1;
    
    @Column(length = 500)
    private String imagenUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Estado estado = Estado.DISPONIBLE;
    
    @OneToMany(mappedBy = "servicio")
    private List<DetalleVenta> ventas = new ArrayList<>();
    
    // Enums
    public enum TipoServicio {
        ENTRENAMIENTO_PERSONAL,
        ASESORIA_NUTRICIONAL,
        CLASE_GRUPAL,
        EVALUACION_FISICA,
        PLAN_ALIMENTICIO
    }
    
    public enum Estado {
        DISPONIBLE, NO_DISPONIBLE
    }
    
    // Constructores
    public Servicio() {}
    
    public Servicio(String codigo, String nombre, BigDecimal precio, TipoServicio tipo) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.tipo = tipo;
    }
    
    // Getters y Setters
    public Long getIdServicio() { return idServicio; }
    public void setIdServicio(Long idServicio) { this.idServicio = idServicio; }
    
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public TipoServicio getTipo() { return tipo; }
    public void setTipo(TipoServicio tipo) { this.tipo = tipo; }
    
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    
    public Integer getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(Integer duracionMinutos) { this.duracionMinutos = duracionMinutos; }
    
    public Integer getCapacidadMaxima() { return capacidadMaxima; }
    public void setCapacidadMaxima(Integer capacidadMaxima) { this.capacidadMaxima = capacidadMaxima; }
    
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
    
    public List<DetalleVenta> getVentas() { return ventas; }
    public void setVentas(List<DetalleVenta> ventas) { this.ventas = ventas; }
    
    // Métodos auxiliares
    public boolean estaDisponible() {
        return estado == Estado.DISPONIBLE;
    }
    
    public String getTipoFormateado() {
        return switch (tipo) {
            case ENTRENAMIENTO_PERSONAL -> "Entrenamiento Personal";
            case ASESORIA_NUTRICIONAL -> "Asesoría Nutricional";
            case CLASE_GRUPAL -> "Clase Grupal";
            case EVALUACION_FISICA -> "Evaluación Física";
            case PLAN_ALIMENTICIO -> "Plan Alimenticio";
        };
    }
}