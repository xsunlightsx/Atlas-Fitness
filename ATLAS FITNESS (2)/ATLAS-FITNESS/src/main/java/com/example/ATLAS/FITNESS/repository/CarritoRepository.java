package com.example.ATLAS.FITNESS.repository;

import com.example.ATLAS.FITNESS.model.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    
    // Método CORREGIDO: findByClienteClienteId (no findByClienteIdCliente)
    Optional<Carrito> findByClienteClienteId(Long clienteId);
    
    // O si prefieres usar @Query
    // @Query("SELECT c FROM Carrito c WHERE c.cliente.clienteId = :clienteId")
    // Optional<Carrito> findByClienteId(@Param("clienteId") Long clienteId);
}