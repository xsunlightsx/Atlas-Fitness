package com.example.ATLAS.FITNESS.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carrito")
public class Carrito {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCarrito;
    
    @OneToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;
    
    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarritoItem> items = new ArrayList<>();
    
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    private LocalDateTime fechaActualizacion = LocalDateTime.now();
    
    @Transient
    public Double getTotal() {
        return items.stream()
                .mapToDouble(item -> item.getSubtotal().doubleValue())
                .sum();
    }
    
    @Transient
    public Integer getTotalItems() {
        return items.stream()
                .mapToInt(CarritoItem::getCantidad)
                .sum();
    }
    
    // Constructores
    public Carrito() {}
    
    public Carrito(Cliente cliente) {
        this.cliente = cliente;
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
    
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
    
    // Métodos auxiliares
    public void agregarItem(Producto producto, Integer cantidad) {
        CarritoItem itemExistente = items.stream()
                .filter(item -> item.getProducto().getIdProducto().equals(producto.getIdProducto()))
                .findFirst()
                .orElse(null);
        
        if (itemExistente != null) {
            itemExistente.setCantidad(itemExistente.getCantidad() + cantidad);
        } else {
            CarritoItem nuevoItem = new CarritoItem(this, producto, cantidad);
            items.add(nuevoItem);
        }
        fechaActualizacion = LocalDateTime.now();
    }
    
    public void eliminarItem(Long idProducto) {
        items.removeIf(item -> item.getProducto().getIdProducto().equals(idProducto));
        fechaActualizacion = LocalDateTime.now();
    }
    
    public void actualizarCantidad(Long idProducto, Integer cantidad) {
        items.stream()
                .filter(item -> item.getProducto().getIdProducto().equals(idProducto))
                .findFirst()
                .ifPresent(item -> {
                    if (cantidad <= 0) {
                        items.remove(item);
                    } else {
                        item.setCantidad(cantidad);
                    }
                });
        fechaActualizacion = LocalDateTime.now();
    }
    
    public void vaciar() {
        items.clear();
        fechaActualizacion = LocalDateTime.now();
    }
}