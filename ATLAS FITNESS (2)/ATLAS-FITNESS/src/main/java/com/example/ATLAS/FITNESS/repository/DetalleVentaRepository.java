package com.example.ATLAS.FITNESS.repository;


import com.example.ATLAS.FITNESS.model.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {
    
    List<DetalleVenta> findByVentaIdVenta(Long idVenta);
    
    @Query("SELECT dv FROM DetalleVenta dv WHERE dv.venta.cliente.idCliente = :idCliente")
    List<DetalleVenta> findByClienteId(Long idCliente);
    
    @Query("SELECT dv FROM DetalleVenta dv WHERE dv.producto.idProducto = :idProducto")
    List<DetalleVenta> findByProductoId(Long idProducto);
    
    @Query("SELECT dv FROM DetalleVenta dv WHERE dv.servicio.idServicio = :idServicio")
    List<DetalleVenta> findByServicioId(Long idServicio);
    
    @Query("SELECT SUM(dv.cantidad) FROM DetalleVenta dv WHERE dv.producto.idProducto = :idProducto")
    Long sumCantidadVendidaByProducto(Long idProducto);
}