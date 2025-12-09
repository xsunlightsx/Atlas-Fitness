package com.example.ATLAS.FITNESS.controller;

import com.example.ATLAS.FITNESS.model.Cliente;
import com.example.ATLAS.FITNESS.model.Membresia;
import com.example.ATLAS.FITNESS.model.Usuario;
import com.example.ATLAS.FITNESS.service.MembresiaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/cliente/membresia")
public class MembresiaController {
    
    private final MembresiaService membresiaService;
    
    public MembresiaController(MembresiaService membresiaService) {
        this.membresiaService = membresiaService;
    }
    
    @GetMapping
    public String mostrarMembresia(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        var membresiaOpt = membresiaService.buscarMembresiaActivaCliente(cliente.getIdCliente());
        
        if (membresiaOpt.isPresent()) {
            Membresia membresia = membresiaOpt.get();
            model.addAttribute("membresia", membresia);
            model.addAttribute("estaActiva", membresia.estaActiva());
            model.addAttribute("estaVencida", membresia.estaVencida());
        } else {
            model.addAttribute("sinMembresia", true);
        }
        
        model.addAttribute("tiposMembresia", Membresia.TipoMembresia.values());
        return "cliente/membresia";
    }
    
    @PostMapping("/contratar")
    public String contratarMembresia(@RequestParam String tipo,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        
        try {
            Membresia.TipoMembresia tipoMembresia = Membresia.TipoMembresia.valueOf(tipo);
            BigDecimal precio = calcularPrecioMembresia(tipoMembresia);
            
            var membresia = membresiaService.crearMembresia(cliente, tipoMembresia, precio);
            
            redirectAttributes.addFlashAttribute("success", 
                "¡Membresía contratada exitosamente! Código: " + membresia.getCodigoMembresia());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/cliente/membresia";
    }
    
    @PostMapping("/renovar")
    public String renovarMembresia(HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        
        try {
            var membresiaOpt = membresiaService.buscarMembresiaActivaCliente(cliente.getIdCliente());
            if (membresiaOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "No tienes una membresía activa");
                return "redirect:/cliente/membresia";
            }
            
            var membresiaRenovada = membresiaService.renovarMembresia(membresiaOpt.get().getIdMembresia());
            
            redirectAttributes.addFlashAttribute("success", 
                "¡Membresía renovada exitosamente! Nueva fecha de vencimiento: " + 
                membresiaRenovada.getFechaFin());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/cliente/membresia";
    }
    
    @PostMapping("/cancelar")
    public String cancelarMembresia(HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        
        try {
            var membresiaOpt = membresiaService.buscarMembresiaActivaCliente(cliente.getIdCliente());
            if (membresiaOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "No tienes una membresía activa");
                return "redirect:/cliente/membresia";
            }
            
            membresiaService.cancelarMembresia(membresiaOpt.get().getIdMembresia());
            
            redirectAttributes.addFlashAttribute("success", "Membresía cancelada exitosamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/cliente/membresia";
    }
    
    private BigDecimal calcularPrecioMembresia(Membresia.TipoMembresia tipo) {
        return switch (tipo) {
            case MENSUAL -> BigDecimal.valueOf(100.00);
            case TRIMESTRAL -> BigDecimal.valueOf(270.00); // 10% descuento
            case SEMESTRAL -> BigDecimal.valueOf(500.00);  // 15% descuento
            case ANUAL -> BigDecimal.valueOf(900.00);      // 25% descuento
        };
    }
}