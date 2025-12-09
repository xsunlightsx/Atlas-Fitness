package com.example.ATLAS.FITNESS.repository;

import com.example.ATLAS.FITNESS.model.Membresia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MembresiaRepository extends JpaRepository<Membresia, Long> {
    
    Optional<Membresia> findByCodigoMembresia(String codigoMembresia);
    List<Membresia> findByClienteIdCliente(Long idCliente);
    List<Membresia> findByEstado(Membresia.Estado estado);
    List<Membresia> findByTipo(Membresia.TipoMembresia tipo);
    
    @Query("SELECT m FROM Membresia m WHERE m.cliente.idCliente = :idCliente AND m.estado = 'ACTIVA'")
    Optional<Membresia> findMembresiaActivaByCliente(Long idCliente);
    
    @Query("SELECT m FROM Membresia m WHERE m.fechaFin < :fecha AND m.estado = 'ACTIVA'")
    List<Membresia> findMembresiasPorVencer(LocalDate fecha);
    
    @Query("SELECT COUNT(m) FROM Membresia m WHERE m.estado = 'ACTIVA'")
    Long countMembresiasActivas();
    
    @Query("SELECT SUM(m.precio) FROM Membresia m WHERE m.fechaInicio >= :inicio AND m.fechaInicio <= :fin")
    Double sumIngresosMembresias(LocalDate inicio, LocalDate fin);
}