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
    private Long idVenta;
    
    @Column(unique = true, nullable = false, length = 20)
    private String codigoVenta;
    
    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cliente", length = 10)
    private TipoCliente tipoCliente = TipoCliente.INVITADO;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_comprobante", length = 10)
    private TipoComprobante tipoComprobante = TipoComprobante.BOLETA;
    
    private String numeroComprobante;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal igv = BigDecimal.ZERO;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", length = 20)
    private MetodoPago metodoPago = MetodoPago.EFECTIVO;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Estado estado = Estado.PENDIENTE;
    
    @Column(name = "fecha_venta", nullable = false)
    private LocalDateTime fechaVenta = LocalDateTime.now();
    
    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;
    
    @Column(name = "usuario_registro", length = 100)
    private String usuarioRegistro;
    
    private String observaciones;
    
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DetalleVenta> detalles = new ArrayList<>();
    
    // Enums
    public enum TipoCliente {
        REGISTRADO, INVITADO
    }
    
    public enum TipoComprobante {
        BOLETA, FACTURA
    }
    
    public enum MetodoPago {
        EFECTIVO, TARJETA_CREDITO, TARJETA_DEBITO, TRANSFERENCIA, YAPE, PLIN, MEMBRESIA
    }
    
    public enum Estado {
        PENDIENTE, PAGADA, CANCELADA, DEVUELTA, ANULADA
    }
    
    // Constructores
    public Venta() {}
    
    public Venta(Cliente cliente, String codigoVenta) {
        this.cliente = cliente;
        this.codigoVenta = codigoVenta;
        this.tipoCliente = cliente != null ? TipoCliente.REGISTRADO : TipoCliente.INVITADO;
    }
    
    // Getters y Setters
    public Long getIdVenta() { return idVenta; }
    public void setIdVenta(Long idVenta) { this.idVenta = idVenta; }
    
    public String getCodigoVenta() { return codigoVenta; }
    public void setCodigoVenta(String codigoVenta) { this.codigoVenta = codigoVenta; }
    
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    
    public TipoCliente getTipoCliente() { return tipoCliente; }
    public void setTipoCliente(TipoCliente tipoCliente) { this.tipoCliente = tipoCliente; }
    
    public TipoComprobante getTipoComprobante() { return tipoComprobante; }
    public void setTipoComprobante(TipoComprobante tipoComprobante) { this.tipoComprobante = tipoComprobante; }
    
    public String getNumeroComprobante() { return numeroComprobante; }
    public void setNumeroComprobante(String numeroComprobante) { this.numeroComprobante = numeroComprobante; }
    
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    
    public BigDecimal getDescuento() { return descuento; }
    public void setDescuento(BigDecimal descuento) { this.descuento = descuento; }
    
    public BigDecimal getIgv() { return igv; }
    public void setIgv(BigDecimal igv) { this.igv = igv; }
    
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    
    public MetodoPago getMetodoPago() { return metodoPago; }
    public void setMetodoPago(MetodoPago metodoPago) { this.metodoPago = metodoPago; }
    
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
    
    public LocalDateTime getFechaVenta() { return fechaVenta; }
    public void setFechaVenta(LocalDateTime fechaVenta) { this.fechaVenta = fechaVenta; }
    
    public LocalDateTime getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }
    
    public String getUsuarioRegistro() { return usuarioRegistro; }
    public void setUsuarioRegistro(String usuarioRegistro) { this.usuarioRegistro = usuarioRegistro; }
    
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    
    public List<DetalleVenta> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleVenta> detalles) { this.detalles = detalles; }
    
    // Métodos auxiliares
    public void calcularTotales() {
        if (detalles != null) {
            this.subtotal = detalles.stream()
                    .map(detalle -> {
                        if (detalle.getSubtotal() == null) {
                            return detalle.getPrecioUnitario() != null && detalle.getCantidad() != null ?
                                   detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad())) :
                                   BigDecimal.ZERO;
                        }
                        return detalle.getSubtotal();
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            this.igv = this.subtotal.multiply(BigDecimal.valueOf(0.18)); // 18% IGV
            this.total = this.subtotal.add(this.igv).subtract(this.descuento != null ? this.descuento : BigDecimal.ZERO);
        } else {
            this.subtotal = BigDecimal.ZERO;
            this.igv = BigDecimal.ZERO;
            this.total = BigDecimal.ZERO;
        }
    }
    
    public void agregarDetalle(DetalleVenta detalle) {
        if (this.detalles == null) {
            this.detalles = new ArrayList<>();
        }
        detalle.setVenta(this);
        this.detalles.add(detalle);
        calcularTotales();
    }
    
    public int getTotalItems() {
        if (detalles == null) return 0;
        return detalles.stream()
                .mapToInt(DetalleVenta::getCantidad)
                .sum();
    }
}