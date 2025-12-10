package com.example.ATLAS.FITNESS.service;

import com.example.ATLAS.FITNESS.model.*;
import com.example.ATLAS.FITNESS.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class CarritoService {
    
    private final CarritoRepository carritoRepository;
    private final CarritoDetalleRepository detalleRepository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;
    
    public CarritoService(CarritoRepository carritoRepository,
                         CarritoDetalleRepository detalleRepository,
                         ProductoRepository productoRepository,
                         ClienteRepository clienteRepository) {
        this.carritoRepository = carritoRepository;
        this.detalleRepository = detalleRepository;
        this.productoRepository = productoRepository;
        this.clienteRepository = clienteRepository;
    }
    
    public Optional<Carrito> obtenerCarritoCliente(Long clienteId) {
        return carritoRepository.findByClienteClienteId(clienteId);
    }
    
    @Transactional
    public Carrito obtenerOCrearCarrito(Long clienteId) {
        return carritoRepository.findByClienteClienteId(clienteId)
                .orElseGet(() -> {
                    Cliente cliente = clienteRepository.findById(clienteId)
                            .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
                    Carrito nuevoCarrito = new Carrito();
                    nuevoCarrito.setCliente(cliente);
                    nuevoCarrito.setEstado("ACTIVO");
                    return carritoRepository.save(nuevoCarrito);
                });
    }
    
    @Transactional
    public void agregarProducto(Long clienteId, Long productoId, Integer cantidad) {
        if (cantidad <= 0) {
            throw new RuntimeException("La cantidad debe ser mayor a 0");
        }
        
        Carrito carrito = obtenerOCrearCarrito(clienteId);
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        if (!producto.estaDisponible()) {
            throw new RuntimeException("Producto no disponible");
        }
        
        // LÍNEA 62 CORREGIDA:
        Optional<CarritoDetalle> detalleExistente = detalleRepository
                .findByCarritoCarritoIdAndProductoIdProducto(carrito.getCarritoId(), productoId);
        
        if (detalleExistente.isPresent()) {
            // Actualizar cantidad
            CarritoDetalle detalle = detalleExistente.get();
            detalle.setCantidad(detalle.getCantidad() + cantidad);
            detalleRepository.save(detalle);
        } else {
            // Crear nuevo detalle
            CarritoDetalle nuevoDetalle = new CarritoDetalle();
            nuevoDetalle.setCarrito(carrito);
            nuevoDetalle.setProducto(producto);
            nuevoDetalle.setCantidad(cantidad);
            nuevoDetalle.setPrecioUnitario(producto.getPrecio());
            detalleRepository.save(nuevoDetalle);
        }
    }
    
    @Transactional
    public void actualizarCantidad(Long clienteId, Long productoId, Integer cantidad) {
        Carrito carrito = obtenerCarritoCliente(clienteId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
        
        if (cantidad <= 0) {
            eliminarProducto(clienteId, productoId);
            return;
        }
        
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        // LÍNEA 94 CORREGIDA:
        CarritoDetalle detalle = detalleRepository
                .findByCarritoCarritoIdAndProductoIdProducto(carrito.getCarritoId(), productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado en el carrito"));
        
        detalle.setCantidad(cantidad);
        detalleRepository.save(detalle);
    }
    
    @Transactional
    public void eliminarProducto(Long clienteId, Long productoId) {
        Carrito carrito = obtenerCarritoCliente(clienteId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
        
        // LÍNEA 107 CORREGIDA:
        CarritoDetalle detalle = detalleRepository
                .findByCarritoCarritoIdAndProductoIdProducto(carrito.getCarritoId(), productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado en el carrito"));
        
        detalleRepository.delete(detalle);
    }
    
    @Transactional
    public void vaciarCarrito(Long clienteId) {
        Carrito carrito = obtenerCarritoCliente(clienteId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
        
        detalleRepository.deleteByCarritoCarritoId(carrito.getCarritoId());
    }
    
    public Integer contarItems(Long clienteId) {
        return obtenerCarritoCliente(clienteId)
                .map(carrito -> detalleRepository.sumCantidadByCarritoId(carrito.getCarritoId()))
                .orElse(0);
    }
    
    public BigDecimal calcularTotal(Long clienteId) {
        return obtenerCarritoCliente(clienteId)
                .map(Carrito::calcularSubtotal)
                .orElse(BigDecimal.ZERO);
    }
}