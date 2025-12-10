package com.example.ATLAS.FITNESS.controller;

import com.example.ATLAS.FITNESS.model.Producto;
import com.example.ATLAS.FITNESS.service.ProductoService;
import com.example.ATLAS.FITNESS.service.CarritoService;
import com.example.ATLAS.FITNESS.model.Cliente;
import com.example.ATLAS.FITNESS.model.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/productos")
public class ProductoController {
    
    private final ProductoService productoService;
    private final CarritoService carritoService;
    
    public ProductoController(ProductoService productoService, CarritoService carritoService) {
        this.productoService = productoService;
        this.carritoService = carritoService;
    }
    
    @GetMapping
    public String listarProductos(@RequestParam(required = false) Long categoriaId,
                                 @RequestParam(required = false) String busqueda,
                                 HttpSession session,
                                 Model model) {
        
        List<Producto> productos;
        
        if (busqueda != null && !busqueda.isEmpty()) {
            productos = productoService.buscarProductos(busqueda);
            model.addAttribute("busqueda", busqueda);
        } else if (categoriaId != null) {
            productos = productoService.listarPorCategoriaId(categoriaId);
            model.addAttribute("categoriaSeleccionada", categoriaId);
        } else {
            productos = productoService.listarProductosDisponibles();
        }
        
        // Obtener contador del carrito si el usuario está logueado
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null) {
            Cliente cliente = (Cliente) session.getAttribute("cliente");
            if (cliente != null) {
                Integer totalItems = carritoService.contarItems(cliente.getClienteId());
                model.addAttribute("totalItemsCarrito", totalItems);
            }
        }
        
        model.addAttribute("productos", productos);
        model.addAttribute("categorias", Arrays.asList(
            new CategoriaInfo(1L, "SUPLEMENTO"),
            new CategoriaInfo(2L, "ACCESORIO"),
            new CategoriaInfo(3L, "ROPA"),
            new CategoriaInfo(4L, "EQUIPO")
        ));
        return "productos/catalogo";
    }
    
    @GetMapping("/{id}")
    public String verDetalleProducto(@PathVariable Long id, Model model, HttpSession session) {
        Producto producto = productoService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        // Obtener contador del carrito si el usuario está logueado
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null) {
            Cliente cliente = (Cliente) session.getAttribute("cliente");
            if (cliente != null) {
                Integer totalItems = carritoService.contarItems(cliente.getClienteId());
                model.addAttribute("totalItemsCarrito", totalItems);
            }
        }
        
        // Productos relacionados (misma categoría)
        List<Producto> relacionados = productoService.listarPorCategoriaId(producto.getCategoriaId())
                .stream()
                .filter(p -> !p.getIdProducto().equals(id) && p.estaDisponible())
                .limit(4)
                .toList();
        
        model.addAttribute("producto", producto);
        model.addAttribute("relacionados", relacionados);
        return "productos/detalle";
    }
    
    // Clase auxiliar para manejar categorías
    private static class CategoriaInfo {
        private Long id;
        private String nombre;
        
        public CategoriaInfo(Long id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }
        
        public Long getId() { return id; }
        public String getNombre() { return nombre; }
    }
}