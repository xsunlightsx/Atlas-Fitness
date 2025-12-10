package com.example.ATLAS.FITNESS.controller;

import com.example.ATLAS.FITNESS.model.*;
import com.example.ATLAS.FITNESS.service.CarritoService;
import com.example.ATLAS.FITNESS.service.ClienteService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.math.BigDecimal;

@Controller
@RequestMapping("/carrito")
public class CarritoController {
    
    private final CarritoService carritoService;
    private final ClienteService clienteService;
    
    public CarritoController(CarritoService carritoService, ClienteService clienteService) {
        this.carritoService = carritoService;
        this.clienteService = clienteService;
    }
    
    // Interceptor para guardar URL antes del login
    @ModelAttribute
    public void preHandle(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        
        // Guardar URL solo si es una ruta protegida y no es login
        if (!requestUri.contains("/auth/") && !requestUri.contains("/carrito/contador")) {
            request.getSession().setAttribute("url_prior_login", requestUri);
        }
    }
    
    @GetMapping("/carrito")
    public String verCarrito(Authentication authentication, Model model, HttpServletRequest request) {
        if (authentication == null || !authentication.isAuthenticated()) {
            request.getSession().setAttribute("url_prior_login", "/carrito/carrito");
            return "redirect:/auth/login";
        }
        
        String username = authentication.getName();
        
        try {
            // Obtener cliente por username
            var clienteOpt = clienteService.buscarPorUsername(username);
            if (clienteOpt.isEmpty()) {
                model.addAttribute("error", "Cliente no encontrado");
                return "carrito/carrito";
            }
            
            Cliente cliente = clienteOpt.get();
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
        
        model.addAttribute("username", username);
        model.addAttribute("isAuthenticated", true);
        model.addAttribute("pageTitle", "Mi Carrito");
        
        return "carrito/carrito";
    }
    
    @GetMapping("/checkout")
    public String checkout(Authentication authentication, Model model, HttpServletRequest request) {
        if (authentication == null || !authentication.isAuthenticated()) {
            request.getSession().setAttribute("url_prior_login", "/carrito/checkout");
            return "redirect:/auth/login";
        }
        
        String username = authentication.getName();
        
        try {
            // Obtener cliente por username
            var clienteOpt = clienteService.buscarPorUsername(username);
            if (clienteOpt.isEmpty()) {
                model.addAttribute("error", "Cliente no encontrado");
                return "carrito/carrito";
            }
            
            Cliente cliente = clienteOpt.get();
            var carrito = carritoService.obtenerCarritoCliente(cliente.getClienteId());
            
            if (carrito.isEmpty() || carrito.get().getDetalles().isEmpty()) {
                return "redirect:/carrito/carrito";
            }
            
            model.addAttribute("carrito", carrito.get());
            
            // Calcular totales
            BigDecimal subtotal = carrito.get().calcularSubtotal();
            BigDecimal envio = BigDecimal.valueOf(15.00);
            BigDecimal total = subtotal.add(envio);
            
            model.addAttribute("subtotal", subtotal);
            model.addAttribute("envio", envio);
            model.addAttribute("total", total);
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al procesar el checkout: " + e.getMessage());
            return "redirect:/carrito/carrito";
        }
        
        model.addAttribute("username", username);
        model.addAttribute("isAuthenticated", true);
        model.addAttribute("pageTitle", "Checkout");
        
        return "carrito/checkout";
    }
    
    @PostMapping("/agregar")
    public String agregarProducto(@RequestParam Long productoId,
                                 @RequestParam(defaultValue = "1") Integer cantidad,
                                 @RequestParam(required = false) String redirect,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes,
                                 HttpServletRequest request) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            request.getSession().setAttribute("url_prior_login", "/carrito");
            return "redirect:/auth/login";
        }
        
        String username = authentication.getName();
        var clienteOpt = clienteService.buscarPorUsername(username);
        
        if (clienteOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Cliente no encontrado");
            return "redirect:/auth/login";
        }
        
        Cliente cliente = clienteOpt.get();
        
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
        
        return "redirect:/carrito/carrito";
    }
    
    @PostMapping("/actualizar")
    public String actualizarCantidad(@RequestParam Long productoId,
                                    @RequestParam Integer cantidad,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes,
                                    HttpServletRequest request) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            request.getSession().setAttribute("url_prior_login", "/carrito/carrito");
            return "redirect:/auth/login";
        }
        
        String username = authentication.getName();
        var clienteOpt = clienteService.buscarPorUsername(username);
        
        if (clienteOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Cliente no encontrado");
            return "redirect:/auth/login";
        }
        
        Cliente cliente = clienteOpt.get();
        
        try {
            carritoService.actualizarCantidad(cliente.getClienteId(), productoId, cantidad);
            redirectAttributes.addFlashAttribute("success", "Cantidad actualizada");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/carrito/carrito";
    }
    
    @PostMapping("/eliminar")
    public String eliminarProducto(@RequestParam Long productoId,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes,
                                  HttpServletRequest request) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            request.getSession().setAttribute("url_prior_login", "/carrito/carrito");
            return "redirect:/auth/login";
        }
        
        String username = authentication.getName();
        var clienteOpt = clienteService.buscarPorUsername(username);
        
        if (clienteOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Cliente no encontrado");
            return "redirect:/auth/login";
        }
        
        Cliente cliente = clienteOpt.get();
        
        try {
            carritoService.eliminarProducto(cliente.getClienteId(), productoId);
            redirectAttributes.addFlashAttribute("success", "Producto eliminado del carrito");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/carrito/carrito";
    }
    
    @PostMapping("/vaciar")
    public String vaciarCarrito(Authentication authentication,
                               RedirectAttributes redirectAttributes,
                               HttpServletRequest request) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            request.getSession().setAttribute("url_prior_login", "/carrito/carrito");
            return "redirect:/auth/login";
        }
        
        String username = authentication.getName();
        var clienteOpt = clienteService.buscarPorUsername(username);
        
        if (clienteOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Cliente no encontrado");
            return "redirect:/auth/login";
        }
        
        Cliente cliente = clienteOpt.get();
        
        try {
            carritoService.vaciarCarrito(cliente.getClienteId());
            redirectAttributes.addFlashAttribute("success", "Carrito vaciado");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/carrito/carrito";
    }
    
    @GetMapping("/contador")
    @ResponseBody
    public String obtenerContadorCarrito(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "0";
        }
        
        String username = authentication.getName();
        var clienteOpt = clienteService.buscarPorUsername(username);
        
        if (clienteOpt.isEmpty()) {
            return "0";
        }
        
        Cliente cliente = clienteOpt.get();
        Integer totalItems = carritoService.contarItems(cliente.getClienteId());
        return totalItems.toString();
    }
}