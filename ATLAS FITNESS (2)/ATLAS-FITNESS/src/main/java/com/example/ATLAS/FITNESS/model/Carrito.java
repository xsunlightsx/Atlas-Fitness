package com.example.ATLAS.FITNESS.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carrito")
public class Carrito {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "carrito_id")
    private Long idCarrito;
    
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
    
    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarritoItem> items = new ArrayList<>();
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    
    @Column(length = 20)
    private String estado = "ACTIVO";
    
    // Constructores
    public Carrito() {}
    
    public Carrito(Cliente cliente) {
        this.cliente = cliente;
        this.estado = "ACTIVO";
        this.fechaCreacion = LocalDateTime.now();
    }
    
    // Getters y Setters
    public Long getIdCarrito() { return idCarrito; }
    public void setIdCarrito(Long idCarrito) { this.idCarrito = idCarrito; }
    
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    
    public List<CarritoItem> getItems() { return items; }
    public void setItems(List<CarritoItem> items) { this.items = items; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    // Métodos utilitarios
    public boolean estaActivo() {
        return "ACTIVO".equals(estado);
    }
    
    public void agregarItem(Producto producto, Integer cantidad) {
        // Buscar si el producto ya está en el carrito
        for (CarritoItem item : items) {
            if (item.getProducto().getIdProducto().equals(producto.getIdProducto())) {
                item.setCantidad(item.getCantidad() + cantidad);
                return;
            }
        }
        
        // Si no existe, crear nuevo item
        CarritoItem nuevoItem = new CarritoItem();
        nuevoItem.setCarrito(this);
        nuevoItem.setProducto(producto);
        nuevoItem.setCantidad(cantidad);
        nuevoItem.setPrecioUnitario(producto.getPrecio());
        items.add(nuevoItem);
    }
    
    public void actualizarCantidad(Long productoId, Integer cantidad) {
        for (CarritoItem item : items) {
            if (item.getProducto().getIdProducto().equals(productoId)) {
                item.setCantidad(cantidad);
                return;
            }
        }
    }
    
    public void eliminarItem(Long productoId) {
        items.removeIf(item -> item.getProducto().getIdProducto().equals(productoId));
    }
    
    public void vaciar() {
        items.clear();
    }
    
    public Integer getTotalItems() {
        return items.stream()
                .mapToInt(CarritoItem::getCantidad)
                .sum();
    }
    
    public BigDecimal getTotal() {
        return items.stream()
                .map(CarritoItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}