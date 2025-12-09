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
    
    // Agregar este método
    Optional<Venta> findByCodigoVenta(String codigoVenta);
    
    List<Venta> findByClienteIdCliente(Long idCliente);
    List<Venta> findByEstado(Venta.Estado estado);
    List<Venta> findByFechaVentaBetween(LocalDateTime inicio, LocalDateTime fin);
    
    @Query("SELECT v FROM Venta v WHERE v.cliente.idCliente = :idCliente ORDER BY v.fechaVenta DESC")
    List<Venta> findHistorialByCliente(@Param("idCliente") Long idCliente);
    
    @Query("SELECT COUNT(v) FROM Venta v WHERE DATE(v.fechaVenta) = CURRENT_DATE")
    Long countVentasHoy();
    
    @Query("SELECT SUM(v.total) FROM Venta v WHERE DATE(v.fechaVenta) = CURRENT_DATE AND v.estado = 'PAGADA'")
    Double sumIngresosHoy();
}