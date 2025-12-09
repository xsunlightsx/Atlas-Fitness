package com.example.ATLAS.FITNESS.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "producto")
public class Producto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProducto;
    
    @Column(unique = true, nullable = false, length = 20)
    private String codigo;
    
    @Column(nullable = false, length = 200)
    private String nombre;
    
    @Column(length = 1000)
    private String descripcion;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Categoria categoria = Categoria.SUPLEMENTO;
    
    private String subcategoria;
    private String marca;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal precioCompra;
    
    private Integer stock = 0;
    private Integer stockMinimo = 5;
    private Integer stockMaximo = 100;
    
    @Column(length = 500)
    private String imagenUrl = "default-product.png";
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Estado estado = Estado.DISPONIBLE;
    
    @Column(nullable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();
    
    private LocalDateTime fechaActualizacion = LocalDateTime.now();
    
    // Enums
    public enum Categoria {
        SUPLEMENTO, ACCESORIO, ROPA, EQUIPO, NUTRICION
    }
    
    public enum Estado {
        DISPONIBLE, AGOTADO, DESCONTINUADO, EN_PEDIDO
    }
    
    // Constructores
    public Producto() {}
    
    public Producto(String codigo, String nombre, BigDecimal precio, Categoria categoria) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
    }
    
    // Getters y Setters
    public Long getIdProducto() { return idProducto; }
    public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }
    
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    
    public String getSubcategoria() { return subcategoria; }
    public void setSubcategoria(String subcategoria) { this.subcategoria = subcategoria; }
    
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    
    public BigDecimal getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(BigDecimal precioCompra) { this.precioCompra = precioCompra; }
    
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    
    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }
    
    public Integer getStockMaximo() { return stockMaximo; }
    public void setStockMaximo(Integer stockMaximo) { this.stockMaximo = stockMaximo; }
    
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
    
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
    
    // Métodos auxiliares
    public boolean estaDisponible() {
        return estado == Estado.DISPONIBLE && stock > 0;
    }
    
    public boolean stockBajo() {
        return stock <= stockMinimo;
    }
}