package com.example.ATLAS.FITNESS.repository;

import com.example.ATLAS.FITNESS.model.Venta;
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
    
    @Query("SELECT SUM(v.total) FROM Venta v WHERE v.fechaVenta >= :fecha")
    Double sumTotalByFechaAfter(@Param("fecha") LocalDateTime fecha);
    
    @Query("SELECT COUNT(v) FROM Venta v WHERE v.fechaVenta >= :fecha")
    Long countByFechaVentaAfter(@Param("fecha") LocalDateTime fecha);
}