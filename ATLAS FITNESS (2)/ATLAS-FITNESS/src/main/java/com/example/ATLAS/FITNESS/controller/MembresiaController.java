package com.example.ATLAS.FITNESS.controller;

import com.example.ATLAS.FITNESS.model.Cliente;
import com.example.ATLAS.FITNESS.model.Membresia;
import com.example.ATLAS.FITNESS.service.MembresiaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;

@Controller
@RequestMapping("/cliente/membresia")
public class MembresiaController extends BaseController {
    
    private final MembresiaService membresiaService;
    
    public MembresiaController(MembresiaService membresiaService) {
        this.membresiaService = membresiaService;
    }
    
    @GetMapping
    public String mostrarMembresia(HttpSession session, Model model) {
        try {
            requireCliente(session);
        } catch (SecurityException e) {
            return "redirect:/auth/login";
        }
        
        Cliente cliente = getCliente(session);
        if (cliente == null) {
            return "redirect:/auth/login";
        }
        
        var membresiaOpt = membresiaService.buscarMembresiaActivaCliente(cliente.getClienteId());
        
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
        try {
            requireCliente(session);
        } catch (SecurityException e) {
            return "redirect:/auth/login";
        }
        
        Cliente cliente = getCliente(session);
        if (cliente == null) {
            return "redirect:/auth/login";
        }
        
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
        try {
            requireCliente(session);
        } catch (SecurityException e) {
            return "redirect:/auth/login";
        }
        
        Cliente cliente = getCliente(session);
        if (cliente == null) {
            return "redirect:/auth/login";
        }
        
        try {
            var membresiaOpt = membresiaService.buscarMembresiaActivaCliente(cliente.getClienteId());
            if (membresiaOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "No tienes una membresía activa");
                return "redirect:/cliente/membresia";
            }
            
            Membresia membresia = membresiaOpt.get();
            
            // Verificar si está vencida o inactiva
            if (!membresia.estaActiva() || membresia.estaVencida()) {
                redirectAttributes.addFlashAttribute("error", 
                    membresia.estaVencida() ? "Tu membresía está vencida" : "Tu membresía no está activa");
                return "redirect:/cliente/membresia";
            }
            
            var membresiaRenovada = membresiaService.renovarMembresia(membresia.getIdMembresia());
            
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
        try {
            requireCliente(session);
        } catch (SecurityException e) {
            return "redirect:/auth/login";
        }
        
        Cliente cliente = getCliente(session);
        if (cliente == null) {
            return "redirect:/auth/login";
        }
        
        try {
            var membresiaOpt = membresiaService.buscarMembresiaActivaCliente(cliente.getClienteId());
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