package com.example.ATLAS.FITNESS.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Carrito_Detalle")
public class CarritoDetalle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detalle_id")
    private Long detalleId;
    
    @ManyToOne
    @JoinColumn(name = "carrito_id", nullable = false)
    private Carrito carrito;
    
    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;
    
    @ManyToOne
    @JoinColumn(name = "servicio_id")
    private Servicio servicio;
    
    @Column(name = "cantidad")
    private Integer cantidad = 1;
    
    @Column(name = "precio_unitario")
    private BigDecimal precioUnitario;
    
    // Constructores
    public CarritoDetalle() {}
    
    public CarritoDetalle(Carrito carrito, Producto producto, Integer cantidad) {
        this.carrito = carrito;
        this.producto = producto;
        this.cantidad = cantidad;
        if (producto != null && producto.getPrecio() != null) {
            this.precioUnitario = producto.getPrecio();
        }
    }
    
    // Getters y Setters
    public Long getDetalleId() { return detalleId; }
    public void setDetalleId(Long detalleId) { this.detalleId = detalleId; }
    
    public Carrito getCarrito() { return carrito; }
    public void setCarrito(Carrito carrito) { this.carrito = carrito; }
    
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    
    public Servicio getServicio() { return servicio; }
    public void setServicio(Servicio servicio) { this.servicio = servicio; }
    
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    
    // Métodos utilitarios
    public BigDecimal calcularSubtotal() {
        if (precioUnitario == null || cantidad == null) {
            return BigDecimal.ZERO;
        }
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
    
    @PrePersist
    @PreUpdate
    protected void onPersist() {
        if (producto != null && precioUnitario == null) {
            precioUnitario = producto.getPrecio();
        }
    }
    
    @Override
    public String toString() {
        return "CarritoDetalle{" +
                "detalleId=" + detalleId +
                ", producto=" + (producto != null ? producto.getNombre() : "null") +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", subtotal=" + calcularSubtotal() +
                '}';
    }
}