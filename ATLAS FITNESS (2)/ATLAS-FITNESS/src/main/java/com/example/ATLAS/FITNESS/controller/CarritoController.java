package com.example.ATLAS.FITNESS.controller;

import com.example.ATLAS.FITNESS.model.*;
import com.example.ATLAS.FITNESS.service.CarritoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

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
        if (cliente == null) {
            return "redirect:/auth/login";
        }
        
        try {
            var carrito = carritoService.obtenerCarritoCliente(cliente.getClienteId());
            
            if (carrito.isPresent() && !carrito.get().getDetalles().isEmpty()) {
                model.addAttribute("carrito", carrito.get());
                
                // Calcular totales
                BigDecimal subtotal = carrito.get().calcularSubtotal();
                BigDecimal envio = BigDecimal.valueOf(15.00);
                BigDecimal total = subtotal.add(envio);
                
                model.addAttribute("subtotal", subtotal);
                model.addAttribute("envio", envio);
                model.addAttribute("total", total);
                model.addAttribute("totalItems", carrito.get().getTotalItems());
            } else {
                model.addAttribute("carritoVacio", true);
                model.addAttribute("subtotal", BigDecimal.ZERO);
                model.addAttribute("envio", BigDecimal.ZERO);
                model.addAttribute("total", BigDecimal.ZERO);
                model.addAttribute("totalItems", 0);
            }
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el carrito: " + e.getMessage());
            model.addAttribute("carritoVacio", true);
        }
        
        return "carrito/carrito";
    }
    
    @PostMapping("/agregar")
    public String agregarProducto(@RequestParam Long productoId,
                                 @RequestParam(defaultValue = "1") Integer cantidad,
                                 @RequestParam(required = false) String redirect,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login?redirect=/carrito";
        }
        
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente == null) {
            return "redirect:/auth/login";
        }
        
        try {
            carritoService.agregarProducto(cliente.getClienteId(), productoId, cantidad);
            redirectAttributes.addFlashAttribute("success", "Producto agregado al carrito");
            
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        // Redirigir dependiendo de la página de origen
        if ("catalogo".equals(redirect)) {
            return "redirect:/productos";
        }
        
        return "redirect:/carrito";
    }
    
    @PostMapping("/actualizar")
    public String actualizarCantidad(@RequestParam Long productoId,
                                    @RequestParam Integer cantidad,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente == null) {
            return "redirect:/auth/login";
        }
        
        try {
            carritoService.actualizarCantidad(cliente.getClienteId(), productoId, cantidad);
            redirectAttributes.addFlashAttribute("success", "Cantidad actualizada");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
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
        if (cliente == null) {
            return "redirect:/auth/login";
        }
        
        try {
            carritoService.eliminarProducto(cliente.getClienteId(), productoId);
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
        if (cliente == null) {
            return "redirect:/auth/login";
        }
        
        try {
            carritoService.vaciarCarrito(cliente.getClienteId());
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
        if (cliente == null) {
            return "0";
        }
        
        Integer totalItems = carritoService.contarItems(cliente.getClienteId());
        return totalItems.toString();
    }
}