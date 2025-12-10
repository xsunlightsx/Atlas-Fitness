package com.example.ATLAS.FITNESS.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;

@Controller
public class HomeController {
    
    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        model.addAttribute("pageTitle", "Inicio");
        
        // Verificar si el usuario está autenticado
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("isAuthenticated", true);
            model.addAttribute("username", authentication.getName());
        } else {
            model.addAttribute("isAuthenticated", false);
        }
        
        return "index";
    }
    
    // Si necesitas un enlace de registro simple que redirija al AuthController
    @GetMapping("/registro")
    public String registro(Model model) {
        // Redirige al registro real en AuthController
        return "redirect:/auth/registro";
    }
    
    @GetMapping("/productos/catalogo")
    public String catalogo(Model model, Authentication authentication) {
        model.addAttribute("pageTitle", "Productos");
        
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("isAuthenticated", true);
            model.addAttribute("username", authentication.getName());
        }
        
        return "productos/catalogo";
    }
    
    @GetMapping("/carrito/carrito")
    public String carrito(Model model, Authentication authentication) {
        model.addAttribute("pageTitle", "Carrito");
        
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("isAuthenticated", true);
            model.addAttribute("username", authentication.getName());
        }
        
        return "carrito/carrito";
    }
    
    @GetMapping("/carrito/checkout")
    public String checkout(Model model, Authentication authentication) {
        model.addAttribute("pageTitle", "Checkout");
        
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("isAuthenticated", true);
            model.addAttribute("username", authentication.getName());
        }
        
        return "carrito/checkout";
    }
    
    @GetMapping("/productos/detalle")
    public String detalle(Model model, Authentication authentication) {
        model.addAttribute("pageTitle", "Detalle de Producto");
        
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("isAuthenticated", true);
            model.addAttribute("username", authentication.getName());
        }
        
        return "productos/detalle";
    }
}