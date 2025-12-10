package com.example.ATLAS.FITNESS.service;

import com.example.ATLAS.FITNESS.dto.CheckoutDTO;
import com.example.ATLAS.FITNESS.model.*;
import com.example.ATLAS.FITNESS.repository.ProductoRepository;
import com.example.ATLAS.FITNESS.repository.VentaRepository;
import com.example.ATLAS.FITNESS.repository.CarritoRepository;
import com.example.ATLAS.FITNESS.repository.ClienteRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class VentaService {
    
    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final CarritoRepository carritoRepository;
    private final ClienteRepository clienteRepository;
    private final CarritoService carritoService;
    
    public VentaService(VentaRepository ventaRepository,
                       ProductoRepository productoRepository,
                       CarritoRepository carritoRepository,
                       ClienteRepository clienteRepository,
                       CarritoService carritoService) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.carritoRepository = carritoRepository;
        this.clienteRepository = clienteRepository;
        this.carritoService = carritoService;
    }
    
    @Transactional
    public Venta procesarVenta(Long idCliente, CheckoutDTO checkoutDTO) {
        // Verificar que el cliente existe
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        
        // Obtener carrito del cliente
        Carrito carrito = carritoRepository.findByClienteClienteId(idCliente)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
        
        // Verificar que el carrito está activo
        if (!"ACTIVO".equals(carrito.getEstado())) {
            throw new RuntimeException("El carrito no está activo");
        }
        
        // Verificar que hay detalles en el carrito
        if (carrito.getDetalles() == null || carrito.getDetalles().isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }
        
        // Verificar stock de todos los productos
        for (CarritoDetalle detalle : carrito.getDetalles()) {
            if (detalle.getProducto() != null) {
                Producto producto = detalle.getProducto();
                if (producto.getStock() < detalle.getCantidad()) {
                    throw new RuntimeException("Stock insuficiente para: " + producto.getNombre());
                }
            }
        }
        
        // Crear la venta
        Venta venta = new Venta();
        venta.setCodigoVenta(generarCodigoVenta());
        venta.setCliente(cliente);
        
        // Método de pago - manejo seguro
        if (checkoutDTO.getMetodoPago() != null && !checkoutDTO.getMetodoPago().isEmpty()) {
            try {
                venta.setMetodoPago(Venta.MetodoPago.valueOf(
                    checkoutDTO.getMetodoPago().toUpperCase().replace(" ", "_")
                ));
            } catch (IllegalArgumentException e) {
                // Método por defecto si no es válido
                venta.setMetodoPago(Venta.MetodoPago.EFECTIVO);
            }
        } else {
            venta.setMetodoPago(Venta.MetodoPago.EFECTIVO);
        }
        
        venta.setObservaciones(checkoutDTO.getObservaciones());
        venta.setFechaVenta(LocalDateTime.now());
        venta.setEstado(Venta.Estado.PENDIENTE);
        
        // Dirección de entrega si es delivery
        if (checkoutDTO.getDireccion() != null && !checkoutDTO.getDireccion().isEmpty()) {
            venta.setDireccionEntrega(checkoutDTO.getDireccion());
            venta.setCostoEnvio(BigDecimal.valueOf(15.00)); // Valor por defecto
        }
        
        // Crear detalles de venta desde el carrito
        List<DetalleVenta> detallesVenta = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        
        for (CarritoDetalle detalleCarrito : carrito.getDetalles()) {
            if (detalleCarrito.getProducto() != null) {
                DetalleVenta detalleVenta = new DetalleVenta();
                detalleVenta.setVenta(venta);
                detalleVenta.setProducto(detalleCarrito.getProducto());
                detalleVenta.setCantidad(detalleCarrito.getCantidad());
                detalleVenta.setPrecioUnitario(detalleCarrito.getPrecioUnitario());
                
                // Calcular subtotal para este detalle
                BigDecimal subtotalDetalle = detalleCarrito.getPrecioUnitario()
                    .multiply(BigDecimal.valueOf(detalleCarrito.getCantidad()));
                detalleVenta.setSubtotal(subtotalDetalle);
                
                detallesVenta.add(detalleVenta);
                subtotal = subtotal.add(subtotalDetalle);
                
                // Actualizar stock del producto
                Producto producto = detalleCarrito.getProducto();
                producto.setStock(producto.getStock() - detalleCarrito.getCantidad());
                
                // Cambiar estado si stock es 0
                if (producto.getStock() == 0) {
                    producto.setEstado(Producto.Estado.AGOTADO);
                }
                
                productoRepository.save(producto);
            }
        }
        
        venta.setDetalles(detallesVenta);
        
        // Calcular totales
        venta.setSubtotal(subtotal);
        
        // Calcular IGV (18%)
        BigDecimal igv = subtotal.multiply(BigDecimal.valueOf(0.18));
        venta.setIgv(igv);
        
        // Total = subtotal + igv + costo envío (si hay)
        BigDecimal total = subtotal.add(igv);
        if (venta.getCostoEnvio() != null) {
            total = total.add(venta.getCostoEnvio());
        }
        venta.setTotal(total);
        
        // Guardar la venta
        Venta ventaGuardada = ventaRepository.save(venta);
        
        // Actualizar estado del carrito a COMPLETADO
        carrito.setEstado("COMPLETADO");
        carritoRepository.save(carrito);
        
        // Crear nuevo carrito vacío para el cliente
        Carrito nuevoCarrito = new Carrito();
        nuevoCarrito.setCliente(cliente);
        nuevoCarrito.setEstado("ACTIVO");
        carritoRepository.save(nuevoCarrito);
        
        // Cambiar estado a COMPLETADA
        ventaGuardada.setEstado(Venta.Estado.COMPLETADA);
        ventaRepository.save(ventaGuardada);
        
        return ventaGuardada;
    }
    
    @Transactional
    public Venta procesarVentaRapida(Long clienteId, Long productoId, Integer cantidad) {
        // Buscar cliente
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        
        // Buscar producto
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        // Verificar stock
        if (producto.getStock() < cantidad) {
            throw new RuntimeException("Stock insuficiente para: " + producto.getNombre());
        }
        
        // Crear venta rápida
        Venta venta = new Venta();
        venta.setCodigoVenta(generarCodigoVenta());
        venta.setCliente(cliente);
        venta.setMetodoPago(Venta.MetodoPago.EFECTIVO);
        venta.setFechaVenta(LocalDateTime.now());
        venta.setEstado(Venta.Estado.PENDIENTE);
        
        // Crear detalle de venta
        DetalleVenta detalle = new DetalleVenta();
        detalle.setVenta(venta);
        detalle.setProducto(producto);
        detalle.setCantidad(cantidad);
        detalle.setPrecioUnitario(producto.getPrecio());
        
        BigDecimal subtotalDetalle = producto.getPrecio()
            .multiply(BigDecimal.valueOf(cantidad));
        detalle.setSubtotal(subtotalDetalle);
        
        List<DetalleVenta> detalles = new ArrayList<>();
        detalles.add(detalle);
        venta.setDetalles(detalles);
        
        // Calcular totales
        venta.setSubtotal(subtotalDetalle);
        
        BigDecimal igv = subtotalDetalle.multiply(BigDecimal.valueOf(0.18));
        venta.setIgv(igv);
        
        BigDecimal total = subtotalDetalle.add(igv);
        venta.setTotal(total);
        
        // Actualizar stock
        producto.setStock(producto.getStock() - cantidad);
        if (producto.getStock() == 0) {
            producto.setEstado(Producto.Estado.AGOTADO);
        }
        productoRepository.save(producto);
        
        // Guardar venta
        Venta ventaGuardada = ventaRepository.save(venta);
        
        // Cambiar estado a COMPLETADA
        ventaGuardada.setEstado(Venta.Estado.COMPLETADA);
        return ventaRepository.save(ventaGuardada);
    }
    
    public List<Venta> obtenerHistorialCliente(Long idCliente) {
        return ventaRepository.findByClienteClienteId(idCliente);
    }
    
    public Optional<Venta> buscarVentaPorCodigo(String codigoVenta) {
        return ventaRepository.findByCodigoVenta(codigoVenta);
    }
    
    public List<Venta> obtenerVentasPendientes() {
        return ventaRepository.findByEstado(Venta.Estado.PENDIENTE);
    }
    
    public List<Venta> obtenerVentasCompletadas() {
        return ventaRepository.findByEstado(Venta.Estado.COMPLETADA);
    }
    
    public List<Venta> obtenerVentasRecientes(int limite) {
        // CORREGIDO: Usando Pageable en lugar de findTopNBy
        Pageable pageable = PageRequest.of(0, limite);
        return ventaRepository.findAllByOrderByFechaVentaDesc(pageable).getContent();
    }
    
    @Transactional
    public void cancelarVenta(Long ventaId) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        
        if (venta.getEstado() == Venta.Estado.COMPLETADA) {
            // Devolver stock
            for (DetalleVenta detalle : venta.getDetalles()) {
                if (detalle.getProducto() != null) {
                    Producto producto = detalle.getProducto();
                    producto.setStock(producto.getStock() + detalle.getCantidad());
                    
                    // Si estaba agotado, cambiar a disponible
                    if (producto.getEstado() == Producto.Estado.AGOTADO) {
                        producto.setEstado(Producto.Estado.DISPONIBLE);
                    }
                    
                    productoRepository.save(producto);
                }
            }
        }
        
        venta.setEstado(Venta.Estado.CANCELADA);
        ventaRepository.save(venta);
    }
    
    public BigDecimal calcularIngresosHoy() {
        LocalDateTime hoy = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        List<Venta> ventasHoy = ventaRepository.findByFechaVentaAfterAndEstado(
            hoy, Venta.Estado.COMPLETADA);
        
        return ventasHoy.stream()
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public Long contarVentasHoy() {
        LocalDateTime hoy = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        return ventaRepository.countByFechaVentaAfterAndEstado(
            hoy, Venta.Estado.COMPLETADA);
    }
    
    public BigDecimal calcularIngresosTotales() {
        List<Venta> ventasCompletadas = ventaRepository.findByEstado(Venta.Estado.COMPLETADA);
        return ventasCompletadas.stream()
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> estadisticas = new HashMap<>();
        
        // Ventas hoy
        estadisticas.put("ventasHoy", contarVentasHoy());
        estadisticas.put("ingresosHoy", calcularIngresosHoy());
        
        // Total general
        estadisticas.put("ventasTotales", ventaRepository.countByEstado(Venta.Estado.COMPLETADA));
        estadisticas.put("ingresosTotales", calcularIngresosTotales());
        
        // Ventas por método de pago
        Map<Venta.MetodoPago, Long> ventasPorMetodo = new HashMap<>();
        for (Venta.MetodoPago metodo : Venta.MetodoPago.values()) {
            Long count = ventaRepository.countByMetodoPagoAndEstado(metodo, Venta.Estado.COMPLETADA);
            ventasPorMetodo.put(metodo, count);
        }
        estadisticas.put("ventasPorMetodo", ventasPorMetodo);
        
        return estadisticas;
    }
    
    private String generarCodigoVenta() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "VEN-" + timestamp.substring(timestamp.length() - 6) + "-" + random;
    }
    
    // Métodos de búsqueda y filtrado
    public List<Venta> buscarVentasPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return ventaRepository.findByFechaVentaBetween(fechaInicio, fechaFin);
    }
    
    public List<Venta> buscarVentasPorClienteYFecha(Long clienteId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return ventaRepository.findByClienteClienteIdAndFechaVentaBetween(
            clienteId, fechaInicio, fechaFin);
    }
    
    @Transactional
    public void actualizarEstadoVenta(Long ventaId, Venta.Estado nuevoEstado) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        
        // Validar transición de estado
        if (venta.getEstado() == Venta.Estado.CANCELADA && 
            (nuevoEstado == Venta.Estado.COMPLETADA || nuevoEstado == Venta.Estado.PENDIENTE)) {
            throw new RuntimeException("No se puede reactivar una venta cancelada");
        }
        
        venta.setEstado(nuevoEstado);
        ventaRepository.save(venta);
    }
}