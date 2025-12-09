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
    
    public List<DetalleVenta> obtenerDetallesPorVenta(Long idVenta) {
        return detalleVentaRepository.findByVentaIdVenta(idVenta);
    }
    
    public List<DetalleVenta> obtenerHistorialComprasCliente(Long idCliente) {
        return detalleVentaRepository.findByClienteId(idCliente);
    }
    
    public Long contarVentasProducto(Long idProducto) {
        return detalleVentaRepository.sumCantidadVendidaByProducto(idProducto);
    }
    
    public List<DetalleVenta> obtenerDetallesPorProducto(Long idProducto) {
        return detalleVentaRepository.findByProductoId(idProducto);
    }
    
    public List<DetalleVenta> obtenerDetallesPorServicio(Long idServicio) {
        return detalleVentaRepository.findByServicioId(idServicio);
    }
}