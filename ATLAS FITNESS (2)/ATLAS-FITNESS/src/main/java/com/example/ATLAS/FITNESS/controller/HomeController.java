package com.example.ATLAS.FITNESS.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("pageTitle", "Inicio");
        return "index";
    }
    
    // Si necesitas un enlace de registro simple que redirija al AuthController
    @GetMapping("/registro")
    public String registro(Model model) {
        // Redirige al registro real en AuthController
        return "redirect:/auth/registro";
    }
    
    // NOTA: Todos los métodos relacionados con cliente (/cliente/*) 
    // deberían estar en ClienteController. Los elimino o comento:
    
    @GetMapping("/productos/catalogo")
    public String catalogo(Model model) {
        model.addAttribute("pageTitle", "Productos");
        return "productos/catalogo";
    }
    
    /*
    @GetMapping("/cliente/perfil")
    public String perfil(Model model) {
        model.addAttribute("pageTitle", "Mi Perfil");
        return "cliente/perfil";
    }
    
    @GetMapping("/cliente/rutinas")
    public String rutinas(Model model) {
        model.addAttribute("pageTitle", "Mis Rutinas");
        return "cliente/rutinas";
    }
    
    @GetMapping("/cliente/membresia")
    public String membresia(Model model) {
        model.addAttribute("pageTitle", "Membresía");
        return "cliente/membresia";
    }
    
    @GetMapping("/cliente/historial")
    public String historial(Model model) {
        model.addAttribute("pageTitle", "Historial");
        return "cliente/historial";
    }
    */
    
    // Deja estos métodos, no parecen tener conflictos
    @GetMapping("/carrito/carrito")
    public String carrito(Model model) {
        model.addAttribute("pageTitle", "Carrito");
        return "carrito/carrito";
    }
    
    @GetMapping("/carrito/checkout")
    public String checkout(Model model) {
        model.addAttribute("pageTitle", "Checkout");
        return "carrito/checkout";
    }
    
    @GetMapping("/productos/detalle")
    public String detalle(Model model) {
        model.addAttribute("pageTitle", "Detalle de Producto");
        return "productos/detalle";
    }
}