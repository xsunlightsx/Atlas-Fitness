package com.example.ATLAS.FITNESS.dto;

import java.math.BigDecimal;

public class CarritoItemDTO {
    private Long idProducto;
    private String nombre;
    private String imagenUrl;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    
    // Constructor por defecto
    public CarritoItemDTO() {}
    
    // Constructor con parámetros
    public CarritoItemDTO(Long idProducto, String nombre, String imagenUrl, 
                         Integer cantidad, BigDecimal precioUnitario) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.imagenUrl = imagenUrl;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = this.getSubtotal();
    }
    
    // Getters y Setters
    public Long getIdProducto() { return idProducto; }
    public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    
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
    
    // toString() para debugging
    @Override
    public String toString() {
        return "CarritoItemDTO{" +
                "idProducto=" + idProducto +
                ", nombre='" + nombre + '\'' +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", subtotal=" + subtotal +
                '}';
    }
}