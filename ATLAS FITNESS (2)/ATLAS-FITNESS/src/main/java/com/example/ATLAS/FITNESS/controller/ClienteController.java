package com.example.ATLAS.FITNESS.controller;

import com.example.ATLAS.FITNESS.model.Cliente;
import com.example.ATLAS.FITNESS.model.Usuario;
import com.example.ATLAS.FITNESS.service.ClienteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.Authentication;

@Controller
@RequestMapping("/cliente")
public class ClienteController {
    
    private final ClienteService clienteService;
    
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }
    
    @GetMapping("/dashboard")
    public String mostrarDashboard(Authentication authentication, HttpSession session, Model model) {
        // Verificar autenticación usando Spring Security
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        
        // Usuario autenticado por Spring Security
        String username = authentication.getName();
        model.addAttribute("username", username);
        model.addAttribute("isAuthenticated", true);
        
        // Obtener usuario de la sesión o del servicio
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            // Aquí deberías obtener el usuario de tu servicio
            // usuario = usuarioService.buscarPorUsername(username);
        }
        
        if (usuario != null && usuario.getClienteId() != null) {
            Cliente cliente = clienteService.buscarPorId(usuario.getClienteId())
                    .orElse(null);
            model.addAttribute("cliente", cliente);
            session.setAttribute("cliente", cliente);
        }
        
        model.addAttribute("pageTitle", "Mi Panel");
        return "cliente/dashboard";
    }
    
    @GetMapping("/rutinas")
    public String mostrarRutinas(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        
        model.addAttribute("username", authentication.getName());
        model.addAttribute("isAuthenticated", true);
        model.addAttribute("pageTitle", "Mis Rutinas");
        
        return "cliente/rutinas";
    }
    
    @GetMapping("/membresias")
    public String mostrarMembresias(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        
        model.addAttribute("username", authentication.getName());
        model.addAttribute("isAuthenticated", true);
        model.addAttribute("pageTitle", "Membresías");
        
        return "cliente/membresias";
    }
    
    @GetMapping("/perfil")
    public String mostrarPerfil(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        
        model.addAttribute("username", authentication.getName());
        model.addAttribute("isAuthenticated", true);
        model.addAttribute("pageTitle", "Mi Perfil");
        
        return "cliente/perfil";
    }
    
    @GetMapping("/historial")
    public String mostrarHistorial(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }
        
        model.addAttribute("username", authentication.getName());
        model.addAttribute("isAuthenticated", true);
        model.addAttribute("pageTitle", "Historial");
        
        return "cliente/historial";
    }

    
}