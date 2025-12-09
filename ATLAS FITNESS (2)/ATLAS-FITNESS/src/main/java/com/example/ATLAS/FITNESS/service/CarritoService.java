package com.example.ATLAS.FITNESS.service;

import com.example.ATLAS.FITNESS.model.Carrito;
import com.example.ATLAS.FITNESS.model.Cliente;
import com.example.ATLAS.FITNESS.model.Producto;
import com.example.ATLAS.FITNESS.repository.CarritoRepository;
import com.example.ATLAS.FITNESS.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CarritoService {
    
    private final CarritoRepository carritoRepository;
    private final ProductoRepository productoRepository;
    private final ClienteService clienteService;
    
    public CarritoService(CarritoRepository carritoRepository,
                         ProductoRepository productoRepository,
                         ClienteService clienteService) {
        this.carritoRepository = carritoRepository;
        this.productoRepository = productoRepository;
        this.clienteService = clienteService;
    }
    
    public Optional<Carrito> obtenerCarritoCliente(Long idCliente) {
        return carritoRepository.findByClienteIdCliente(idCliente);
    }
    
    @Transactional
    public Carrito obtenerOCrearCarrito(Long idCliente) {
        return carritoRepository.findByClienteIdCliente(idCliente)
                .orElseGet(() -> {
                    Cliente cliente = clienteService.buscarPorId(idCliente)
                            .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
                    Carrito nuevoCarrito = new Carrito(cliente);
                    return carritoRepository.save(nuevoCarrito);
                });
    }
    
    @Transactional
    public Carrito agregarProducto(Long idCliente, Long idProducto, Integer cantidad) {
        Carrito carrito = obtenerOCrearCarrito(idCliente);
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        if (!producto.estaDisponible()) {
            throw new RuntimeException("Producto no disponible");
        }
        
        if (producto.getStock() < cantidad) {
            throw new RuntimeException("Stock insuficiente");
        }
        
        carrito.agregarItem(producto, cantidad);
        return carritoRepository.save(carrito);
    }
    
    @Transactional
    public Carrito actualizarCantidad(Long idCliente, Long idProducto, Integer cantidad) {
        Carrito carrito = obtenerCarritoCliente(idCliente)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
        
        if (cantidad <= 0) {
            carrito.eliminarItem(idProducto);
        } else {
            Producto producto = productoRepository.findById(idProducto)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            
            if (producto.getStock() < cantidad) {
                throw new RuntimeException("Stock insuficiente");
            }
            
            carrito.actualizarCantidad(idProducto, cantidad);
        }
        
        return carritoRepository.save(carrito);
    }
    
    @Transactional
    public Carrito eliminarProducto(Long idCliente, Long idProducto) {
        Carrito carrito = obtenerCarritoCliente(idCliente)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
        
        carrito.eliminarItem(idProducto);
        return carritoRepository.save(carrito);
    }
    
    @Transactional
    public void vaciarCarrito(Long idCliente) {
        Carrito carrito = obtenerCarritoCliente(idCliente)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
        
        carrito.vaciar();
        carritoRepository.save(carrito);
    }
    
    @Transactional
    public Double calcularTotal(Long idCliente) {
        return obtenerCarritoCliente(idCliente)
                .map(Carrito::getTotal)
                .orElse(0.0);
    }
    
    public Integer contarItems(Long idCliente) {
        return obtenerCarritoCliente(idCliente)
                .map(carrito -> carrito.getTotalItems())
                .orElse(0);
    }
}