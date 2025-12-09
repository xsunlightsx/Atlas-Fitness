package com.example.ATLAS.FITNESS.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "carrito_item")
public class CarritoItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "carrito_id", nullable = false)
    private Carrito carrito;
    
    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
    
    private Integer cantidad = 1;
    
    @Column(name = "precio_unitario", precision = 10, scale = 2)
    private BigDecimal precioUnitario;
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Carrito getCarrito() { return carrito; }
    public void setCarrito(Carrito carrito) { this.carrito = carrito; }
    
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    
    // Métodos utilitarios
    public BigDecimal getSubtotal() {
        if (precioUnitario == null || cantidad == null) {
            return BigDecimal.ZERO;
        }
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
}