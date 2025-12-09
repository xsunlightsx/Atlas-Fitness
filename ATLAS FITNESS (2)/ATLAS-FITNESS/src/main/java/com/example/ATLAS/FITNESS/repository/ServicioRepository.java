package com.example.ATLAS.FITNESS.repository;

import com.example.ATLAS.FITNESS.model.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {
    
    Optional<Servicio> findByCodigo(String codigo);
    List<Servicio> findByTipo(Servicio.TipoServicio tipo);
    List<Servicio> findByEstado(Servicio.Estado estado);
    
    @Query("SELECT s FROM Servicio s WHERE s.estado = 'DISPONIBLE'")
    List<Servicio> findServiciosDisponibles();
    
    @Query("SELECT s FROM Servicio s WHERE " +
           "LOWER(s.nombre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.descripcion) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Servicio> buscarServicios(String query);
}