package com.example.ATLAS.FITNESS.repository;

import com.example.ATLAS.FITNESS.model.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    
    Optional<Carrito> findByClienteClienteId(Long clienteId);
    
    // Opcional: Si necesitas buscar por cliente y estado
    @Query("SELECT c FROM Carrito c WHERE c.cliente.clienteId = :clienteId AND c.estado = :estado")
    Optional<Carrito> findByClienteClienteIdAndEstado(
        @Param("clienteId") Long clienteId, 
        @Param("estado") String estado);
    
    // O usando Spring Data JPA naming convention (si tienes el campo 'estado' en Carrito)
    // Optional<Carrito> findByClienteClienteIdAndEstado(Long clienteId, String estado);
}