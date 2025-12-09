package com.example.ATLAS.FITNESS.controller;

import com.example.ATLAS.FITNESS.model.Cliente;
import com.example.ATLAS.FITNESS.model.Usuario;
import com.example.ATLAS.FITNESS.service.ClienteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cliente")
public class ClienteController {
    
    private final ClienteService clienteService;
    
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }
    
    @GetMapping("/perfil")
    public String mostrarPerfil(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        
        Cliente cliente = clienteService.buscarPorUsername(usuario.getUsername())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        
        model.addAttribute("cliente", cliente);
        return "cliente/perfil";
    }
    
    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(@ModelAttribute Cliente clienteActualizado,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        
        try {
            Cliente cliente = clienteService.buscarPorUsername(usuario.getUsername())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
            
            clienteService.actualizarPerfil(cliente.getIdCliente(), clienteActualizado);
            session.setAttribute("cliente", cliente);
            
            redirectAttributes.addFlashAttribute("success", "Perfil actualizado exitosamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar perfil: " + e.getMessage());
        }
        
        return "redirect:/cliente/perfil";
    }
    
    @GetMapping("/historial")
    public String mostrarHistorial(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        
        Cliente cliente = clienteService.buscarPorUsername(usuario.getUsername())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        
        model.addAttribute("cliente", cliente);
        return "cliente/historial";
    }
    
    // ⚠️ ELIMINADO: Este método está DUPLICADO en MembresiaController
    // @GetMapping("/membresia")
    // public String mostrarMembresia(HttpSession session, Model model) {
    //     Usuario usuario = (Usuario) session.getAttribute("usuario");
    //     if (usuario == null) {
    //         return "redirect:/auth/login";
    //     }
    //     
    //     Cliente cliente = clienteService.buscarPorUsername(usuario.getUsername())
    //             .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    //     
    //     model.addAttribute("cliente", cliente);
    //     return "cliente/membresia";
    // }
    
    // ⚠️ ELIMINADO: Este método está DUPLICADO en MembresiaController
    // @PostMapping("/membresia/renovar")
    // public String renovarMembresia(HttpSession session,
    //                               RedirectAttributes redirectAttributes) {
    //     Usuario usuario = (Usuario) session.getAttribute("usuario");
    //     if (usuario == null) {
    //         return "redirect:/auth/login";
    //     }
    //     
    //     try {
    //         // Lógica para renovar membresía
    //         redirectAttributes.addFlashAttribute("success", "Membresía renovada exitosamente");
    //     } catch (RuntimeException e) {
    //         redirectAttributes.addFlashAttribute("error", "Error al renovar membresía: " + e.getMessage());
    //     }
    //     
    //     return "redirect:/cliente/membresia";
    // }
    
    @GetMapping("/rutinas")
    public String mostrarRutinas(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        
        Cliente cliente = clienteService.buscarPorUsername(usuario.getUsername())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        
        model.addAttribute("cliente", cliente);
        return "cliente/rutinas";
    }
    
    @PostMapping("/rutinas/progreso")
    public String registrarProgreso(@RequestParam String progreso,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        
        try {
            // Lógica para registrar progreso
            redirectAttributes.addFlashAttribute("success", "Progreso registrado exitosamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar progreso: " + e.getMessage());
        }
        
        return "redirect:/cliente/rutinas";
    }
}