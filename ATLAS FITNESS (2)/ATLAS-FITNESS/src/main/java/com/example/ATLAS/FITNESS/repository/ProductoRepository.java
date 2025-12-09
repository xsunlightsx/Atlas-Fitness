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
    List<Producto> findByCategoria(Producto.Categoria categoria);
    List<Producto> findByEstado(Producto.Estado estado);
    List<Producto> findByStockLessThan(Integer stock);
    
    @Query("SELECT p FROM Producto p WHERE p.estado = 'DISPONIBLE' AND p.stock > 0")
    List<Producto> findProductosDisponibles();
    
    @Query("SELECT p FROM Producto p WHERE " +
           "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.marca) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Producto> buscarProductos(String query);
    
    @Query("SELECT p FROM Producto p WHERE p.stock <= p.stockMinimo")
    List<Producto> findProductosStockBajo();
    
    List<Producto> findByPrecioBetween(Double precioMin, Double precioMax);
}