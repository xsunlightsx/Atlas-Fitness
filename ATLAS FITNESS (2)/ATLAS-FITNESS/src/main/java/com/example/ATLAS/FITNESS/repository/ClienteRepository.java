package com.example.ATLAS.FITNESS.repository;

import com.example.ATLAS.FITNESS.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    Optional<Cliente> findByDni(String dni);
    
    @Query("SELECT c FROM Cliente c WHERE c.email = :email")
    Optional<Cliente> buscarPorEmail(@Param("email") String email);
    
    Optional<Cliente> findByEmail(String email);
    
    List<Cliente> findByEstado(String estado);
    
    @Query("SELECT COUNT(c) FROM Cliente c WHERE c.estado = 'ACTIVO'")
    Long countClientesActivos();
    
    @Query("SELECT c FROM Cliente c WHERE c.nombre LIKE %:nombre% OR c.apellido LIKE %:nombre%")
    List<Cliente> buscarPorNombre(@Param("nombre") String nombre);
    
    // Buscar por username del usuario asociado
    @Query("SELECT c FROM Cliente c WHERE c.clienteId IN " +
           "(SELECT u.clienteId FROM Usuario u WHERE u.username = :username)")
    Optional<Cliente> buscarPorUsername(@Param("username") String username);
    
    // Buscar cliente con usuario cargado (JOIN FETCH)
    @Query("SELECT c FROM Cliente c LEFT JOIN FETCH Usuario u ON c.clienteId = u.clienteId " +
           "WHERE u.username = :username")
    Optional<Cliente> buscarPorUsernameConUsuario(@Param("username") String username);
}