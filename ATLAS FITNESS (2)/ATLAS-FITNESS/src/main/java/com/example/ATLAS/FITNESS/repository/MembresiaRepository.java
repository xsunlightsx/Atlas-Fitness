package com.example.ATLAS.FITNESS.repository;

import com.example.ATLAS.FITNESS.model.Membresia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MembresiaRepository extends JpaRepository<Membresia, Long> {
    
    @Query("SELECT m FROM Membresia m WHERE m.cliente.clienteId = :clienteId AND m.estado = 'ACTIVA'")
    Optional<Membresia> findMembresiaActivaByCliente(@Param("clienteId") Long clienteId);
    
    List<Membresia> findByClienteClienteId(Long clienteId);
    
    @Query("SELECT m FROM Membresia m WHERE m.fechaFin <= :fechaLimite AND m.estado = 'ACTIVA'")
    List<Membresia> findMembresiasPorVencer(@Param("fechaLimite") LocalDate fechaLimite);
    
    @Query("SELECT COUNT(m) FROM Membresia m WHERE m.estado = 'ACTIVA'")
    Long countMembresiasActivas();
    
    Optional<Membresia> findByCodigoMembresia(String codigoMembresia);
    
    @Query("SELECT m FROM Membresia m WHERE m.cliente.clienteId = :clienteId AND m.estado = 'ACTIVA' AND CURRENT_DATE BETWEEN m.fechaInicio AND m.fechaFin")
    Optional<Membresia> findMembresiaVigenteByCliente(@Param("clienteId") Long clienteId);
    
    // Método para buscar por estado
    List<Membresia> findByEstado(String estado);
    
    // Método para buscar por tipo
    List<Membresia> findByTipo(String tipo);
}