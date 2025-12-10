package com.example.ATLAS.FITNESS.controller;

import com.example.ATLAS.FITNESS.model.Cliente;
import com.example.ATLAS.FITNESS.model.Membresia;
import com.example.ATLAS.FITNESS.service.ClienteService;
import com.example.ATLAS.FITNESS.service.MembresiaService;
import com.example.ATLAS.FITNESS.model.Usuario;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.Optional;

@Controller
@RequestMapping("/cliente")
public class ClienteController {
    
    private final ClienteService clienteService;
    private final MembresiaService membresiaService;
    
    public ClienteController(ClienteService clienteService, MembresiaService membresiaService) {
        this.clienteService = clienteService;
        this.membresiaService = membresiaService;
    }
    
    // === DASHBOARD ===
    @GetMapping("/dashboard")
    public String mostrarDashboard(Authentication authentication, HttpSession session, Model model) {
        if (!estaAutenticado(authentication)) {
            return "redirect:/auth/login";
        }
        
        String username = authentication.getName();
        model.addAttribute("username", username);
        model.addAttribute("isAuthenticated", true);
        
        // Obtener usuario de la sesión
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null && usuario.getClienteId() != null) {
            Optional<Cliente> cliente = clienteService.buscarPorId(usuario.getClienteId());
            cliente.ifPresent(c -> {
                model.addAttribute("cliente", c);
                session.setAttribute("cliente", c);
            });
        }
        
        model.addAttribute("pageTitle", "Mi Panel");
        return "cliente/dashboard";
    }
    
    // === PERFIL ===
    @GetMapping("/perfil")
    public String mostrarPerfil(Authentication authentication, Model model) {
        if (!estaAutenticado(authentication)) {
            return "redirect:/auth/login";
        }
        
        model.addAttribute("username", authentication.getName());
        model.addAttribute("isAuthenticated", true);
        model.addAttribute("pageTitle", "Mi Perfil");
        
        return "cliente/perfil";
    }
    
    // === RUTINAS ===
    @GetMapping("/rutinas")
    public String mostrarRutinas(Authentication authentication, Model model) {
        if (!estaAutenticado(authentication)) {
            return "redirect:/auth/login";
        }
        
        model.addAttribute("username", authentication.getName());
        model.addAttribute("isAuthenticated", true);
        model.addAttribute("pageTitle", "Mis Rutinas");
        
        return "cliente/rutinas";
    }
    
    // === MEMBRESÍA (singular) - Detalle de MI membresía ===
  @GetMapping("/membresia")
public String mostrarMembresia(Authentication authentication, Model model) {
    System.out.println("=== ACCESANDO A /cliente/membresia ===");
    System.out.println("Usuario: " + authentication.getName());
    
    // Datos MÍNIMOS para que la página funcione
    model.addAttribute("username", authentication.getName());
    model.addAttribute("isAuthenticated", true);
    model.addAttribute("pageTitle", "Mi Membresía");
    model.addAttribute("membresiaActual", "PRO");
    model.addAttribute("precioActual", "$49");
    model.addAttribute("estado", "Activa");
    model.addAttribute("fechaInicio", "15/01/2024");
    model.addAttribute("fechaVencimiento", "15/12/2024");
    model.addAttribute("diasRestantes", 30);
    model.addAttribute("porcentajeCiclo", 75);
    
    return "cliente/membresia";
}
    
    // === MEMBRESÍAS (plural) - Ver todos los planes ===
    @GetMapping("/membresias")
    public String verPlanesMembresias(Model model, Authentication authentication) {
        model.addAttribute("pageTitle", "Planes de Membresía");
        model.addAttribute("tiposMembresia", Membresia.TipoMembresia.values());
        
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            model.addAttribute("username", username);
            model.addAttribute("isAuthenticated", true);
            
            Optional<Cliente> clienteOpt = clienteService.buscarPorUsername(username);
            if (clienteOpt.isPresent()) {
                Cliente cliente = clienteOpt.get();
                Optional<Membresia> membresiaOpt = membresiaService.buscarMembresiaActivaCliente(cliente.getClienteId());
                
                if (membresiaOpt.isPresent()) {
                    Membresia membresia = membresiaOpt.get();
                    model.addAttribute("membresiaActual", membresia);
                    model.addAttribute("membresiaTipo", membresia.getTipo().toString());
                }
            }
        }
        
        // Precios
        model.addAttribute("planBasico", BigDecimal.valueOf(29.00));
        model.addAttribute("planPro", BigDecimal.valueOf(49.00));
        model.addAttribute("planPremium", BigDecimal.valueOf(79.00));
        
        return "cliente/membresias"; // templates/cliente/membresias.html
    }
    
    // === HISTORIAL ===
    @GetMapping("/historial")
    public String mostrarHistorial(Authentication authentication, Model model) {
        if (!estaAutenticado(authentication)) {
            return "redirect:/auth/login";
        }
        
        model.addAttribute("username", authentication.getName());
        model.addAttribute("isAuthenticated", true);
        model.addAttribute("pageTitle", "Historial");
        
        return "cliente/historial";
    }
    
    // === CONTRATAR MEMBRESÍA ===
    @PostMapping("/membresia/contratar")
    public String contratarMembresia(@RequestParam String tipo,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes,
                                    HttpServletRequest request) {
        
        if (!estaAutenticado(authentication)) {
            request.getSession().setAttribute("url_prior_login", "/cliente/membresia");
            return "redirect:/auth/login";
        }
        
        String username = authentication.getName();
        Optional<Cliente> clienteOpt = clienteService.buscarPorUsername(username);
        
        if (clienteOpt.isEmpty()) {
            return "redirect:/auth/login";
        }
        
        Cliente cliente = clienteOpt.get();
        
        try {
            BigDecimal precio = switch (tipo.toLowerCase()) {
                case "pro" -> BigDecimal.valueOf(49.00);
                case "premium" -> BigDecimal.valueOf(79.00);
                default -> BigDecimal.valueOf(29.00); // básico
            };
            
            // Lógica para crear membresía
            Membresia membresia = membresiaService.crearMembresia(cliente, 
                Membresia.TipoMembresia.MENSUAL, precio);
            
            redirectAttributes.addFlashAttribute("success", 
                "¡Membresía " + tipo.toUpperCase() + " contratada exitosamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/cliente/membresia";
    }
    
    // === MÉTODO AUXILIAR ===
    private boolean estaAutenticado(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated();
    }
}