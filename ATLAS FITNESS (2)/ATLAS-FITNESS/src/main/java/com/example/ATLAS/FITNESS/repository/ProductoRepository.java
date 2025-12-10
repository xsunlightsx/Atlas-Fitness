package com.example.ATLAS.FITNESS.repository;

import com.example.ATLAS.FITNESS.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    Optional<Producto> findByCodigo(String codigo);
    List<Producto> findByCategoriaId(Long categoriaId);
    List<Producto> findByEstado(Producto.Estado estado);
    List<Producto> findByStockLessThan(Integer stock);
    
    @Query("SELECT p FROM Producto p WHERE p.estado = 'DISPONIBLE' AND p.stock > 0")
    List<Producto> findProductosDisponibles();
    
    @Query("SELECT p FROM Producto p WHERE " +
           "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Producto> buscarProductos(String query);
    
    @Query("SELECT p FROM Producto p WHERE p.stock <= 5") // Stock mínimo de 5
    List<Producto> findProductosStockBajo();
    
    @Query("SELECT p FROM Producto p WHERE p.precio BETWEEN :precioMin AND :precioMax")
    List<Producto> findByPrecioBetween(Double precioMin, Double precioMax);
}