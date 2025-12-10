package com.example.ATLAS.FITNESS.repository;

import com.example.ATLAS.FITNESS.model.Venta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    
    List<Venta> findByClienteClienteId(Long clienteId);
    
    Optional<Venta> findByCodigoVenta(String codigoVenta);
    
    List<Venta> findByEstado(Venta.Estado estado);
    
    List<Venta> findByFechaVentaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    List<Venta> findByClienteClienteIdAndFechaVentaBetween(Long clienteId, LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    List<Venta> findByFechaVentaAfterAndEstado(LocalDateTime fecha, Venta.Estado estado);
    
    Long countByFechaVentaAfterAndEstado(LocalDateTime fecha, Venta.Estado estado);
    
    Long countByEstado(Venta.Estado estado);
    
    Long countByMetodoPagoAndEstado(Venta.MetodoPago metodoPago, Venta.Estado estado);
    
    List<Venta> findByClienteClienteIdOrderByFechaVentaDesc(Long clienteId);
    
    // Método NECESARIO para el método obtenerVentasRecientes
    Page<Venta> findAllByOrderByFechaVentaDesc(Pageable pageable);
    
    @Query("SELECT SUM(v.total) FROM Venta v WHERE v.fechaVenta >= :fecha")
    Double sumTotalByFechaAfter(@Param("fecha") LocalDateTime fecha);
    
    @Query("SELECT COUNT(v) FROM Venta v WHERE v.fechaVenta >= :fecha")
    Long countByFechaVentaAfter(@Param("fecha") LocalDateTime fecha);
}