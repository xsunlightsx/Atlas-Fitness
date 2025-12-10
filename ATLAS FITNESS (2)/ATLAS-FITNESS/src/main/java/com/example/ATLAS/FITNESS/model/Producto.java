package com.example.ATLAS.FITNESS.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "producto")
public class Producto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "producto_id")
    private Long idProducto;
    
    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;
    
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(name = "categoria_id")
    private Long categoriaId;
    
    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;
    
    @Column(name = "precio_oferta", precision = 10, scale = 2)
    private BigDecimal precioOferta;
    
    @Column(name = "sku", unique = true, length = 50)
    private String sku;
    
    @Column(name = "codigo", length = 50)
    private String codigo;
    
    @Column(name = "stock")
    private Integer stock = 0;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20)
    private Estado estado = Estado.DISPONIBLE;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaRegistro = LocalDateTime.now();
    
    // Enum de estado
    public enum Estado {
        DISPONIBLE,
        AGOTADO,
        DESCONTINUADO
    }
    
    // Constructores
    public Producto() {}
    
    public Producto(String nombre, BigDecimal precio, Long categoriaId) {
        this.nombre = nombre;
        this.precio = precio;
        this.categoriaId = categoriaId;
        this.estado = Estado.DISPONIBLE;
        this.fechaRegistro = LocalDateTime.now();
    }
    
    // Getters y Setters
    public Long getIdProducto() { return idProducto; }
    public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }
    
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    
    public BigDecimal getPrecioOferta() { return precioOferta; }
    public void setPrecioOferta(BigDecimal precioOferta) { this.precioOferta = precioOferta; }
    
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
    
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    
    // Métodos auxiliares
    public boolean estaDisponible() {
        return estado == Estado.DISPONIBLE && stock > 0;
    }
    
    public String getCategoria() {
        // Devuelve un string basado en el ID de categoría
        if (categoriaId == null) return "SIN_CATEGORIA";
        switch (categoriaId.intValue()) {
            case 1: return "SUPLEMENTO";
            case 2: return "ACCESORIO";
            case 3: return "ROPA";
            case 4: return "EQUIPO";
            default: return "OTRO";
        }
    }
}