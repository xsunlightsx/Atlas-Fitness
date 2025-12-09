package com.example.ATLAS.FITNESS.service;

import com.example.ATLAS.FITNESS.model.Producto;
import com.example.ATLAS.FITNESS.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {
    
    private final ProductoRepository productoRepository;
    
    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }
    
    public List<Producto> listarProductosDisponibles() {
        return productoRepository.findProductosDisponibles();
    }
    
    public List<Producto> buscarProductos(String query) {
        return productoRepository.buscarProductos(query);
    }
    
    public List<Producto> listarPorCategoria(Producto.Categoria categoria) {
        return productoRepository.findByCategoria(categoria);
    }
    
    public List<Producto> listarProductosStockBajo() {
        return productoRepository.findProductosStockBajo();
    }
    
    public Optional<Producto> buscarPorId(Long id) {
        return productoRepository.findById(id);
    }
    
    public Optional<Producto> buscarPorCodigo(String codigo) {
        return productoRepository.findByCodigo(codigo);
    }
    
    public List<Producto> listarProductosDestacados() {
        return productoRepository.findAll().stream()
                .filter(Producto::estaDisponible)
                .limit(8)
                .toList();
    }
    
    public List<Producto> listarSuplementos() {
        return productoRepository.findByCategoria(Producto.Categoria.SUPLEMENTO)
                .stream()
                .filter(Producto::estaDisponible)
                .toList();
    }
    
    public List<Producto> listarAccesorios() {
        return productoRepository.findByCategoria(Producto.Categoria.ACCESORIO)
                .stream()
                .filter(Producto::estaDisponible)
                .toList();
    }
    
    public boolean verificarStock(Long idProducto, Integer cantidad) {
        return productoRepository.findById(idProducto)
                .map(producto -> producto.getStock() >= cantidad)
                .orElse(false);
    }
}