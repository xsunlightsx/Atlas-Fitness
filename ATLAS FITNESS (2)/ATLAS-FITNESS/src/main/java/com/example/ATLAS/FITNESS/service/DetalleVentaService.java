package com.example.ATLAS.FITNESS.service;

import com.example.ATLAS.FITNESS.model.DetalleVenta;
import com.example.ATLAS.FITNESS.repository.DetalleVentaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DetalleVentaService {
    
    private final DetalleVentaRepository detalleVentaRepository;
    
    public DetalleVentaService(DetalleVentaRepository detalleVentaRepository) {
        this.detalleVentaRepository = detalleVentaRepository;
    }
    
    // Usar método derivado automático en lugar de @Query
    public List<DetalleVenta> obtenerDetallesPorCliente(Long clienteId) {
        return detalleVentaRepository.findByVentaClienteClienteId(clienteId);
    }
    
    // Métodos básicos CRUD
    public List<DetalleVenta> obtenerTodos() {
        return detalleVentaRepository.findAll();
    }
    
    public DetalleVenta obtenerPorId(Long id) {
        return detalleVentaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Detalle de venta no encontrado"));
    }
    
    public DetalleVenta guardar(DetalleVenta detalleVenta) {
        return detalleVentaRepository.save(detalleVenta);
    }
    
    public void eliminar(Long id) {
        detalleVentaRepository.deleteById(id);
    }
}