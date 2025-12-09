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
public class ClienteController extends BaseController {
    
    private final ClienteService clienteService;
    
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }
    
    @GetMapping("/perfil")
    public String mostrarPerfil(HttpSession session, Model model) {
        try {
            requireCliente(session);
        } catch (SecurityException e) {
            return "redirect:/auth/login";
        }
        
        Usuario usuario = getUsuario(session);
        Cliente cliente = getCliente(session);
        
        // Si el cliente no está en sesión, buscarlo
        if (cliente == null) {
            cliente = clienteService.buscarPorUsername(usuario.getUsername())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
            session.setAttribute("cliente", cliente);
        }
        
        model.addAttribute("cliente", cliente);
        return "cliente/perfil";
    }
    
    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(@ModelAttribute Cliente clienteActualizado,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        try {
            requireCliente(session);
        } catch (SecurityException e) {
            return "redirect:/auth/login";
        }
        
        try {
            // 1. Obtener cliente actual de BD
            Usuario usuario = getUsuario(session);
            Cliente clienteBD = clienteService.buscarPorUsername(usuario.getUsername())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
            
            // 2. Copiar solo los campos permitidos del formulario
            clienteBD.setTelefono(clienteActualizado.getTelefono());
            clienteBD.setDireccion(clienteActualizado.getDireccion());
            clienteBD.setFechaNacimiento(clienteActualizado.getFechaNacimiento());
            clienteBD.setGenero(clienteActualizado.getGenero());
            clienteBD.setAltura(clienteActualizado.getAltura());
            clienteBD.setPeso(clienteActualizado.getPeso());
            clienteBD.setObjetivo(clienteActualizado.getObjetivo());
            // NO copiar: dni, nombre, apellido, email (deben venir de Usuario)
            
            // 3. Actualizar en BD
            Cliente clienteActualizadoBD = clienteService.actualizarPerfil(clienteBD);
            
            // 4. Actualizar sesión
            session.setAttribute("cliente", clienteActualizadoBD);
            
            redirectAttributes.addFlashAttribute("success", "Perfil actualizado exitosamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar perfil: " + e.getMessage());
        }
        
        return "redirect:/cliente/perfil";
    }
    
    @GetMapping("/historial")
    public String mostrarHistorial(HttpSession session, Model model) {
        try {
            requireCliente(session);
        } catch (SecurityException e) {
            return "redirect:/auth/login";
        }
        
        Cliente cliente = getCliente(session);
        if (cliente == null) {
            return "redirect:/auth/login";
        }
        
        model.addAttribute("cliente", cliente);
        return "cliente/historial";
    }
    
    @GetMapping("/rutinas")
    public String mostrarRutinas(HttpSession session, Model model) {
        try {
            requireCliente(session);
        } catch (SecurityException e) {
            return "redirect:/auth/login";
        }
        
        Cliente cliente = getCliente(session);
        if (cliente == null) {
            return "redirect:/auth/login";
        }
        
        model.addAttribute("cliente", cliente);
        return "cliente/rutinas";
    }
    
    @PostMapping("/rutinas/progreso")
    public String registrarProgreso(@RequestParam String progreso,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        try {
            requireCliente(session);
        } catch (SecurityException e) {
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