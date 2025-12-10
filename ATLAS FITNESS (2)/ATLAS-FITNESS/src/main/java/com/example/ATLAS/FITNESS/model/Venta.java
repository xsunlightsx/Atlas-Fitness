package com.example.ATLAS.FITNESS.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "venta")
public class Venta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "venta_id")
    private Long idVenta;
    
    @Column(name = "codigo_venta", unique = true, nullable = false, length = 20)
    private String codigoVenta;
    
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", length = 20)
    private MetodoPago metodoPago;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Estado estado = Estado.PENDIENTE;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal subtotal;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal igv;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal costoEnvio;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal total;
    
    @Column(name = "fecha_venta")
    private LocalDateTime fechaVenta;
    
    @Column(columnDefinition = "TEXT")
    private String observaciones;
    
    @Column(name = "direccion_entrega", length = 200)
    private String direccionEntrega;
    
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detalles = new ArrayList<>();
    
    // Enums
    public enum MetodoPago {
        EFECTIVO,
        TARJETA_CREDITO,
        TARJETA_DEBITO,
        TRANSFERENCIA,
        YAPE,
        PLIN
    }
    
    public enum Estado {
        PENDIENTE,
        PAGADA,
        COMPLETADA,
        CANCELADA,
        REEMBOLSADA
    }
    
    // Constructores
    public Venta() {}
    
    public Venta(Cliente cliente, MetodoPago metodoPago) {
        this.cliente = cliente;
        this.metodoPago = metodoPago;
        this.fechaVenta = LocalDateTime.now();
        this.estado = Estado.PENDIENTE;
    }
    
    // Getters y Setters
    public Long getIdVenta() { return idVenta; }
    public void setIdVenta(Long idVenta) { this.idVenta = idVenta; }
    
    public String getCodigoVenta() { return codigoVenta; }
    public void setCodigoVenta(String codigoVenta) { this.codigoVenta = codigoVenta; }
    
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    
    public MetodoPago getMetodoPago() { return metodoPago; }
    public void setMetodoPago(MetodoPago metodoPago) { this.metodoPago = metodoPago; }
    
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
    
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    
    public BigDecimal getIgv() { return igv; }
    public void setIgv(BigDecimal igv) { this.igv = igv; }
    
    public BigDecimal getCostoEnvio() { return costoEnvio; }
    public void setCostoEnvio(BigDecimal costoEnvio) { this.costoEnvio = costoEnvio; }
    
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    
    public LocalDateTime getFechaVenta() { return fechaVenta; }
    public void setFechaVenta(LocalDateTime fechaVenta) { this.fechaVenta = fechaVenta; }
    
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    
    public String getDireccionEntrega() { return direccionEntrega; }
    public void setDireccionEntrega(String direccionEntrega) { this.direccionEntrega = direccionEntrega; }
    
    public List<DetalleVenta> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleVenta> detalles) { this.detalles = detalles; }
    
    // Métodos utilitarios
    public boolean estaPagada() {
        return Estado.PAGADA.equals(estado) || Estado.COMPLETADA.equals(estado);
    }
    
    public void calcularTotal() {
        if (detalles == null || detalles.isEmpty()) {
            this.total = BigDecimal.ZERO;
            return;
        }
        
        this.total = detalles.stream()
                .map(detalle -> {
                    if (detalle.getPrecioUnitario() == null || detalle.getCantidad() == null) {
                        return BigDecimal.ZERO;
                    }
                    return detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}