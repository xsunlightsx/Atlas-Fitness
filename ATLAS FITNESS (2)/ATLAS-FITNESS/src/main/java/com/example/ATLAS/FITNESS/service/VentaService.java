package com.example.ATLAS.FITNESS.service;

import com.example.ATLAS.FITNESS.dto.CheckoutDTO;
import com.example.ATLAS.FITNESS.model.*;
import com.example.ATLAS.FITNESS.repository.ProductoRepository;
import com.example.ATLAS.FITNESS.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VentaService {
    
    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final CarritoService carritoService;
    private final ClienteService clienteService;
    private final EmailService emailService;
    
    public VentaService(VentaRepository ventaRepository,
                       ProductoRepository productoRepository,
                       CarritoService carritoService,
                       ClienteService clienteService,
                       EmailService emailService) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.carritoService = carritoService;
        this.clienteService = clienteService;
        this.emailService = emailService;
    }
    
    @Transactional
    public Venta procesarVenta(Long idCliente, CheckoutDTO checkoutDTO) {
        Carrito carrito = carritoService.obtenerCarritoCliente(idCliente)
                .orElseThrow(() -> new RuntimeException("Carrito vacío"));
        
        if (carrito.getItems().isEmpty()) {
            throw new RuntimeException("No hay items en el carrito");
        }
        
        Cliente cliente = clienteService.buscarPorId(idCliente)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        
        // Crear venta
        Venta venta = new Venta();
        venta.setCodigoVenta(generarCodigoVenta());
        venta.setCliente(cliente);
        
        // CORREGIDO: Convertir String a Enum
        try {
            venta.setMetodoPago(Venta.MetodoPago.valueOf(checkoutDTO.getMetodoPago()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Método de pago inválido: " + checkoutDTO.getMetodoPago());
        }
        
        venta.setObservaciones(checkoutDTO.getObservaciones());
        venta.setFechaVenta(LocalDateTime.now());
        
        // Crear detalles de venta
        venta.setDetalles(new ArrayList<>());
        for (CarritoItem item : carrito.getItems()) {
            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setProducto(item.getProducto());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getPrecioUnitario());
            // El subtotal se calculará en la entidad
            
            venta.getDetalles().add(detalle);
            
            // Actualizar stock
            Producto producto = item.getProducto();
            if (producto.getStock() != null) {
                producto.setStock(producto.getStock() - item.getCantidad());
                productoRepository.save(producto);
            }
        }
        
        // Calcular total
        venta.setTotal(carrito.getTotal());
        
        // CORREGIDO: Usar Enum en lugar de String
        venta.setEstado(Venta.Estado.COMPLETADA);
        
        Venta ventaGuardada = ventaRepository.save(venta);
        
        // Vaciar carrito
        carritoService.vaciarCarrito(idCliente);
        
        // Enviar email de confirmación (si tienes el servicio)
        try {
            emailService.enviarEmailConfirmacionVenta(
                    cliente.getEmail(),
                    cliente.getNombreCompleto(),
                    ventaGuardada
            );
        } catch (Exception e) {
            // Log error pero no fallar la venta
            System.err.println("Error enviando email: " + e.getMessage());
        }
        
        return ventaGuardada;
    }
    
    public List<Venta> obtenerHistorialCliente(Long idCliente) {
        return ventaRepository.findByClienteClienteId(idCliente);
    }
    
    public Optional<Venta> buscarVentaPorCodigo(String codigoVenta) {
        return ventaRepository.findByCodigoVenta(codigoVenta);
    }
    
    private String generarCodigoVenta() {
        return "VEN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    public BigDecimal calcularIngresosHoy() {
        LocalDateTime hoy = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        Double ingresos = ventaRepository.sumTotalByFechaAfter(hoy);
        return ingresos != null ? BigDecimal.valueOf(ingresos) : BigDecimal.ZERO;
    }
    
    public Long contarVentasHoy() {
        LocalDateTime hoy = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        return ventaRepository.countByFechaVentaAfter(hoy);
    }
    
    // Método alternativo para convertir String a Enum de forma segura
    private Venta.MetodoPago obtenerMetodoPago(String metodoPagoStr) {
        try {
            return Venta.MetodoPago.valueOf(metodoPagoStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Si el método no es válido, usar uno por defecto
            return Venta.MetodoPago.EFECTIVO;
        }
    }
    
    // Método para validar y convertir estado
    private Venta.Estado obtenerEstado(String estadoStr) {
        try {
            return Venta.Estado.valueOf(estadoStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Venta.Estado.PENDIENTE;
        }
    }
}