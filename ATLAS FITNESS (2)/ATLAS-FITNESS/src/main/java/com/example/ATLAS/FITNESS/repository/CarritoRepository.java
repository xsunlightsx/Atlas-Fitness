package com.example.ATLAS.FITNESS.repository;

import com.example.ATLAS.FITNESS.model.Carrito;
import com.example.ATLAS.FITNESS.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    Optional<Carrito> findByCliente(Cliente cliente);
    Optional<Carrito> findByClienteIdCliente(Long idCliente);
    void deleteByCliente(Cliente cliente);
}