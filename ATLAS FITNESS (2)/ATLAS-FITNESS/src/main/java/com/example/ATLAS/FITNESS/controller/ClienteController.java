package com.example.ATLAS.FITNESS.controller;

import com.example.ATLAS.FITNESS.model.Cliente;
import com.example.ATLAS.FITNESS.model.Usuario;
import com.example.ATLAS.FITNESS.service.ClienteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cliente")
public class ClienteController {
    
    private final ClienteService clienteService;
    
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }
    
    @GetMapping("/dashboard")
    public String mostrarDashboard(HttpSession session, Model model) {
        // Verificar autenticación
        Object usuarioObj = session.getAttribute("usuario");
        if (usuarioObj == null) {
            return "redirect:/auth/login";
        }
        
        Usuario usuario = (Usuario) usuarioObj;
        model.addAttribute("usuario", usuario);
        
        // Obtener cliente si existe
        if (usuario.getClienteId() != null) {
            Cliente cliente = clienteService.buscarPorId(usuario.getClienteId())
                    .orElse(null);
            model.addAttribute("cliente", cliente);
            session.setAttribute("cliente", cliente);
        }
        
        return "cliente/dashboard";
    }
    
    @GetMapping("/rutinas")
    public String mostrarRutinas(HttpSession session, Model model) {
        Object usuarioObj = session.getAttribute("usuario");
        if (usuarioObj == null) {
            return "redirect:/auth/login";
        }
        
        Usuario usuario = (Usuario) usuarioObj;
        model.addAttribute("usuario", usuario);
        return "cliente/rutinas";
    }
    
    @GetMapping("/membresias")
    public String mostrarMembresias(HttpSession session, Model model) {
        Object usuarioObj = session.getAttribute("usuario");
        if (usuarioObj == null) {
            return "redirect:/auth/login";
        }
        
        Usuario usuario = (Usuario) usuarioObj;
        model.addAttribute("usuario", usuario);
        return "cliente/membresias";
    }
}