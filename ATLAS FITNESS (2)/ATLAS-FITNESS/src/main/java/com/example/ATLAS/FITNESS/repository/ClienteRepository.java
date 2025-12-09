package com.example.ATLAS.FITNESS.repository;

import com.example.ATLAS.FITNESS.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByDni(String dni);
    Optional<Cliente> findByUsuarioUsername(String username);
    Optional<Cliente> findByEmail(String email);
    List<Cliente> findByEstado(Cliente.Estado estado);
    
    @Query("SELECT COUNT(c) FROM Cliente c WHERE c.estado = 'ACTIVO'")
    Long countClientesActivos();
    
    @Query("SELECT c FROM Cliente c WHERE c.nombre LIKE %:nombre% OR c.apellido LIKE %:nombre%")
    List<Cliente> buscarPorNombre(String nombre);
}