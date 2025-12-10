package com.example.ATLAS.FITNESS.repository;

import com.example.ATLAS.FITNESS.model.CarritoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarritoDetalleRepository extends JpaRepository<CarritoDetalle, Long> {
    
    List<CarritoDetalle> findByCarritoCarritoId(Long carritoId);
    
    // Método CORREGIDO: usa "producto.idProducto" (no "producto.productoId")
    Optional<CarritoDetalle> findByCarritoCarritoIdAndProductoIdProducto(Long carritoId, Long productoId);
    
    // O si prefieres usar @Query (recomendado para claridad):
    // @Query("SELECT cd FROM CarritoDetalle cd WHERE cd.carrito.carritoId = :carritoId AND cd.producto.idProducto = :productoId")
    // Optional<CarritoDetalle> findByCarritoAndProducto(
    //     @Param("carritoId") Long carritoId, 
    //     @Param("productoId") Long productoId);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM CarritoDetalle cd WHERE cd.carrito.carritoId = :carritoId")
    void deleteByCarritoCarritoId(@Param("carritoId") Long carritoId);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM CarritoDetalle cd WHERE cd.detalleId = :detalleId")
    void deleteByDetalleId(@Param("detalleId") Long detalleId);
    
    @Query("SELECT SUM(cd.cantidad) FROM CarritoDetalle cd WHERE cd.carrito.carritoId = :carritoId")
    Integer sumCantidadByCarritoId(@Param("carritoId") Long carritoId);
}