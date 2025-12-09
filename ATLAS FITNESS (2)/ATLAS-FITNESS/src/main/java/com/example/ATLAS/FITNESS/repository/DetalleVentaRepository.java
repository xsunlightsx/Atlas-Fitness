package com.example.ATLAS.FITNESS.repository;

import com.example.ATLAS.FITNESS.model.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {
    
    // CORREGIR: Cambiar 'idCliente' por 'clienteId'
    @Query("SELECT dv FROM DetalleVenta dv WHERE dv.venta.cliente.clienteId = :clienteId")
    List<DetalleVenta> findByClienteId(@Param("clienteId") Long clienteId);
    
    // O si prefieres el método derivado automático
    List<DetalleVenta> findByVentaClienteClienteId(Long clienteId);
}