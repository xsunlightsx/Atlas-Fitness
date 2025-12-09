package com.example.ATLAS.FITNESS.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "detalle_venta")
public class DetalleVenta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDetalle;
    
    @ManyToOne
    @JoinColumn(name = "id_venta", nullable = false)
    private Venta venta;
    
    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Producto producto;
    
    @ManyToOne
    @JoinColumn(name = "id_servicio")
    private Servicio servicio;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_item", length = 10)
    private TipoItem tipoItem;
    
    @Column(nullable = false)
    private Integer cantidad = 1;
    
    @Column(name = "precio_unitario", precision = 10, scale = 2)
    private BigDecimal precioUnitario;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal subtotal;
    
    // Enums
    public enum TipoItem {
        PRODUCTO, SERVICIO
    }
    
    // Constructores
    public DetalleVenta() {}
    
    public DetalleVenta(Venta venta, Producto producto, Integer cantidad) {
        this.venta = venta;
        this.producto = producto;
        this.tipoItem = TipoItem.PRODUCTO;
        this.cantidad = cantidad;
        this.precioUnitario = producto.getPrecio();
        this.subtotal = this.getSubtotal();
    }
    
    public DetalleVenta(Venta venta, Servicio servicio, Integer cantidad) {
        this.venta = venta;
        this.servicio = servicio;
        this.tipoItem = TipoItem.SERVICIO;
        this.cantidad = cantidad;
        this.precioUnitario = servicio.getPrecio();
        this.subtotal = this.getSubtotal();
    }
    
    // Getters y Setters
    public Long getIdDetalle() { return idDetalle; }
    public void setIdDetalle(Long idDetalle) { this.idDetalle = idDetalle; }
    
    public Venta getVenta() { return venta; }
    public void setVenta(Venta venta) { this.venta = venta; }
    
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { 
        this.producto = producto;
        if (producto != null) {
            this.tipoItem = TipoItem.PRODUCTO;
            this.precioUnitario = producto.getPrecio();
        }
    }
    
    public Servicio getServicio() { return servicio; }
    public void setServicio(Servicio servicio) { 
        this.servicio = servicio;
        if (servicio != null) {
            this.tipoItem = TipoItem.SERVICIO;
            this.precioUnitario = servicio.getPrecio();
        }
    }
    
    public TipoItem getTipoItem() { return tipoItem; }
    public void setTipoItem(TipoItem tipoItem) { this.tipoItem = tipoItem; }
    
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { 
        this.cantidad = cantidad;
        this.subtotal = this.getSubtotal();
    }
    
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { 
        this.precioUnitario = precioUnitario;
        this.subtotal = this.getSubtotal();
    }
    
    public BigDecimal getSubtotal() { 
        if (precioUnitario == null || cantidad == null) {
            return BigDecimal.ZERO;
        }
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
    
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
    // Métodos auxiliares
    public String getNombreItem() {
        if (tipoItem == TipoItem.PRODUCTO && producto != null) {
            return producto.getNombre();
        } else if (tipoItem == TipoItem.SERVICIO && servicio != null) {
            return servicio.getNombre();
        }
        return "Item no disponible";
    }
    
    public String getDescripcionItem() {
        if (tipoItem == TipoItem.PRODUCTO && producto != null) {
            return producto.getDescripcion();
        } else if (tipoItem == TipoItem.SERVICIO && servicio != null) {
            return servicio.getDescripcion();
        }
        return "";
    }
    
    public String getCodigoItem() {
        if (tipoItem == TipoItem.PRODUCTO && producto != null) {
            return producto.getCodigo();
        } else if (tipoItem == TipoItem.SERVICIO && servicio != null) {
            return servicio.getCodigo();
        }
        return "";
    }
}