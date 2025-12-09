package com.example.ATLAS.FITNESS.controller;

import com.example.ATLAS.FITNESS.model.Producto;
import com.example.ATLAS.FITNESS.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/productos")
public class ProductoController {
    
    private final ProductoService productoService;
    
    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }
    
    @GetMapping
    public String listarProductos(@RequestParam(required = false) String categoria,
                                 @RequestParam(required = false) String busqueda,
                                 Model model) {
        List<Producto> productos;
        
        if (busqueda != null && !busqueda.isEmpty()) {
            productos = productoService.buscarProductos(busqueda);
            model.addAttribute("busqueda", busqueda);
        } else if (categoria != null && !categoria.isEmpty()) {
            try {
                Producto.Categoria cat = Producto.Categoria.valueOf(categoria.toUpperCase());
                productos = productoService.listarPorCategoria(cat);
                model.addAttribute("categoriaSeleccionada", cat);
            } catch (IllegalArgumentException e) {
                productos = productoService.listarProductosDisponibles();
            }
        } else {
            productos = productoService.listarProductosDisponibles();
        }
        
        model.addAttribute("productos", productos);
        model.addAttribute("categorias", Producto.Categoria.values());
        return "productos/catalogo";
    }
    
    @GetMapping("/{id}")
    public String verDetalleProducto(@PathVariable Long id, Model model) {
        Producto producto = productoService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        // Productos relacionados (misma categoría)
        List<Producto> relacionados = productoService.listarPorCategoria(producto.getCategoria())
                .stream()
                .filter(p -> !p.getIdProducto().equals(id) && p.estaDisponible())
                .limit(4)
                .toList();
        
        model.addAttribute("producto", producto);
        model.addAttribute("relacionados", relacionados);
        return "productos/detalle";
    }
    
    @GetMapping("/suplementos")
    public String listarSuplementos(Model model) {
        List<Producto> suplementos = productoService.listarSuplementos();
        model.addAttribute("productos", suplementos);
        model.addAttribute("titulo", "Suplementos");
        model.addAttribute("categoria", Producto.Categoria.SUPLEMENTO);
        return "productos/catalogo";
    }
    
    @GetMapping("/accesorios")
    public String listarAccesorios(Model model) {
        List<Producto> accesorios = productoService.listarAccesorios();
        model.addAttribute("productos", accesorios);
        model.addAttribute("titulo", "Accesorios");
        model.addAttribute("categoria", Producto.Categoria.ACCESORIO);
        return "productos/catalogo";
    }
    
    @GetMapping("/ropa")
    public String listarRopa(Model model) {
        List<Producto> productos = productoService.listarPorCategoria(Producto.Categoria.ROPA)
                .stream()
                .filter(Producto::estaDisponible)
                .toList();
        model.addAttribute("productos", productos);
        model.addAttribute("titulo", "Ropa Deportiva");
        model.addAttribute("categoria", Producto.Categoria.ROPA);
        return "productos/catalogo";
    }
    
    @GetMapping("/destacados")
    public String listarDestacados(Model model) {
        List<Producto> destacados = productoService.listarProductosDestacados();
        model.addAttribute("productos", destacados);
        model.addAttribute("titulo", "Productos Destacados");
        return "productos/catalogo";
    }
}