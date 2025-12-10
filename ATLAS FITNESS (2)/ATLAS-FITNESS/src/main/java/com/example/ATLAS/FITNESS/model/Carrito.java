package com.example.ATLAS.FITNESS.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Carrito")
public class Carrito {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "carrito_id")
    private Long carritoId;
    
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "estado")
    private String estado = "ACTIVO";
    
    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CarritoDetalle> detalles = new ArrayList<>();
    
    // Constructores
    public Carrito() {}
    
    public Carrito(Cliente cliente) {
        this.cliente = cliente;
        this.estado = "ACTIVO";
        this.fechaCreacion = LocalDateTime.now();
    }
    
    // Getters y Setters
    public Long getCarritoId() { return carritoId; }
    public void setCarritoId(Long carritoId) { this.carritoId = carritoId; }
    
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public List<CarritoDetalle> getDetalles() { return detalles; }
    public void setDetalles(List<CarritoDetalle> detalles) { this.detalles = detalles; }
    
    // Métodos de negocio
    public BigDecimal calcularSubtotal() {
        return detalles.stream()
            .map(CarritoDetalle::calcularSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public Integer getTotalItems() {
        return detalles.stream()
            .mapToInt(CarritoDetalle::getCantidad)
            .sum();
    }
    
    public void agregarDetalle(Producto producto, Integer cantidad) {
        // Buscar si ya existe el producto en el carrito
        for (CarritoDetalle detalle : detalles) {
            if (detalle.getProducto() != null && 
                detalle.getProducto().getIdProducto().equals(producto.getIdProducto())) {
                detalle.setCantidad(detalle.getCantidad() + cantidad);
                return;
            }
        }
        
        // Si no existe, crear nuevo detalle
        CarritoDetalle nuevoDetalle = new CarritoDetalle();
        nuevoDetalle.setCarrito(this);
        nuevoDetalle.setProducto(producto);
        nuevoDetalle.setCantidad(cantidad);
        nuevoDetalle.setPrecioUnitario(producto.getPrecio());
        detalles.add(nuevoDetalle);
    }
    
    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
    }
    
    @Override
    public String toString() {
        return "Carrito{" +
                "carritoId=" + carritoId +
                ", cliente=" + (cliente != null ? cliente.getNombreCompleto() : "null") +
                ", totalItems=" + getTotalItems() +
                ", subtotal=" + calcularSubtotal() +
                '}';
    }
}