package com.example.ATLAS.FITNESS.service;

import com.example.ATLAS.FITNESS.dto.CheckoutDTO;
import com.example.ATLAS.FITNESS.model.*;
import com.example.ATLAS.FITNESS.repository.ProductoRepository;
import com.example.ATLAS.FITNESS.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional; // <-- IMPORT AÑADIDO
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
        venta.setTipoCliente(Venta.TipoCliente.REGISTRADO);
        venta.setMetodoPago(Venta.MetodoPago.valueOf(checkoutDTO.getMetodoPago()));
        venta.setObservaciones(checkoutDTO.getObservaciones());
        venta.setUsuarioRegistro(cliente.getNombreCompleto());
        
        // Crear detalles de venta
        for (CarritoItem item : carrito.getItems()) {
            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setProducto(item.getProducto());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getPrecioUnitario());
            detalle.setSubtotal(detalle.getSubtotal());
            
            venta.getDetalles().add(detalle);
            
            // Actualizar stock
            Producto producto = item.getProducto();
            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto);
        }
        
        // Calcular totales
        venta.calcularTotales();
        venta.setEstado(Venta.Estado.PAGADA);
        venta.setFechaPago(LocalDateTime.now());
        
        Venta ventaGuardada = ventaRepository.save(venta);
        
        // Vaciar carrito
        carritoService.vaciarCarrito(idCliente);
        
        // Enviar email de confirmación
        emailService.enviarEmailConfirmacionVenta(
                cliente.getEmail(),
                cliente.getNombre(),
                ventaGuardada
        );
        
        return ventaGuardada;
    }
    
    public List<Venta> obtenerHistorialCliente(Long idCliente) {
        return ventaRepository.findHistorialByCliente(idCliente);
    }
    
    public Optional<Venta> buscarVentaPorCodigo(String codigoVenta) {
        // Método alternativo si findByCodigoVenta no existe
        return ventaRepository.findAll().stream()
                .filter(v -> v.getCodigoVenta().equals(codigoVenta))
                .findFirst();
    }
    
    // Método alternativo más eficiente (debes crear este método en el repository)
    public Venta buscarVentaPorCodigoVenta(String codigoVenta) {
        return ventaRepository.findAll().stream()
                .filter(v -> v.getCodigoVenta() != null && 
                            v.getCodigoVenta().equals(codigoVenta))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con código: " + codigoVenta));
    }
    
    private String generarCodigoVenta() {
        return "VEN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    public BigDecimal calcularIngresosHoy() {
        Double ingresos = ventaRepository.sumIngresosHoy();
        return ingresos != null ? BigDecimal.valueOf(ingresos) : BigDecimal.ZERO;
    }
    
    public Long contarVentasHoy() {
        return ventaRepository.countVentasHoy();
    }
}