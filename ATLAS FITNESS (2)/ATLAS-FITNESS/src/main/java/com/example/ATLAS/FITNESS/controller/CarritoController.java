package com.example.ATLAS.FITNESS.controller;

import com.example.ATLAS.FITNESS.model.Cliente;
import com.example.ATLAS.FITNESS.model.Usuario;
import com.example.ATLAS.FITNESS.service.CarritoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/carrito")
public class CarritoController {
    
    private final CarritoService carritoService;
    
    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }
    
    @GetMapping
    public String verCarrito(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login?redirect=/carrito";
        }
        
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        var carrito = carritoService.obtenerCarritoCliente(cliente.getIdCliente());
        
        if (carrito.isPresent()) {
            model.addAttribute("carrito", carrito.get());
            model.addAttribute("totalItems", carrito.get().getTotalItems());
            model.addAttribute("total", carrito.get().getTotal());
        } else {
            model.addAttribute("carritoVacio", true);
        }
        
        return "carrito/carrito";
    }
    
    @PostMapping("/agregar")
    public String agregarProducto(@RequestParam Long productoId,
                                 @RequestParam(defaultValue = "1") Integer cantidad,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login?redirect=/carrito";
        }
        
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        
        try {
            carritoService.agregarProducto(cliente.getIdCliente(), productoId, cantidad);
            redirectAttributes.addFlashAttribute("success", "Producto agregado al carrito");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/carrito";
    }
    
    @PostMapping("/actualizar")
    public String actualizarCantidad(@RequestParam Long productoId,
                                    @RequestParam Integer cantidad,
                                    HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        carritoService.actualizarCantidad(cliente.getIdCliente(), productoId, cantidad);
        
        return "redirect:/carrito";
    }
    
    @PostMapping("/eliminar")
    public String eliminarProducto(@RequestParam Long productoId,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        
        try {
            carritoService.eliminarProducto(cliente.getIdCliente(), productoId);
            redirectAttributes.addFlashAttribute("success", "Producto eliminado del carrito");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/carrito";
    }
    
    @PostMapping("/vaciar")
    public String vaciarCarrito(HttpSession session,
                               RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        
        try {
            carritoService.vaciarCarrito(cliente.getIdCliente());
            redirectAttributes.addFlashAttribute("success", "Carrito vaciado");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/carrito";
    }
    
    @GetMapping("/contador")
    @ResponseBody
    public String obtenerContadorCarrito(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "0";
        }
        
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        Integer totalItems = carritoService.contarItems(cliente.getIdCliente());
        return totalItems.toString();
    }
}