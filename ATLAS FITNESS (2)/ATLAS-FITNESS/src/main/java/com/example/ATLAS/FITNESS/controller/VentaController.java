package com.example.ATLAS.FITNESS.controller;

import com.example.ATLAS.FITNESS.dto.CheckoutDTO;
import com.example.ATLAS.FITNESS.model.Cliente;
import com.example.ATLAS.FITNESS.model.Usuario;
import com.example.ATLAS.FITNESS.service.VentaService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/checkout")
public class VentaController {
    
    private final VentaService ventaService;
    
    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }
    
    @GetMapping
    public String mostrarCheckout(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login?redirect=/checkout";
        }
        
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        
        // Crear DTO con datos del cliente
        CheckoutDTO checkoutDTO = new CheckoutDTO();
        checkoutDTO.setNombre(cliente.getNombreCompleto());
        checkoutDTO.setEmail(cliente.getEmail());
        checkoutDTO.setTelefono(cliente.getTelefono());
        checkoutDTO.setDireccion(cliente.getDireccion());
        
        model.addAttribute("checkoutDTO", checkoutDTO);
        model.addAttribute("cliente", cliente);
        return "carrito/checkout";
    }
    
    @PostMapping("/procesar")
    public String procesarCheckout(@ModelAttribute @Valid CheckoutDTO checkoutDTO,
                                  BindingResult result,
                                  HttpSession session,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        
        if (result.hasErrors()) {
            Cliente cliente = (Cliente) session.getAttribute("cliente");
            model.addAttribute("cliente", cliente);
            return "carrito/checkout";
        }
        
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        
        try {
            var venta = ventaService.procesarVenta(cliente.getIdCliente(), checkoutDTO);
            redirectAttributes.addFlashAttribute("venta", venta);
            redirectAttributes.addFlashAttribute("success", 
                "¡Compra realizada exitosamente! Código: " + venta.getCodigoVenta());
            return "redirect:/checkout/comprobante/" + venta.getCodigoVenta();
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("cliente", cliente);
            return "carrito/checkout";
        }
    }
    
    @GetMapping("/comprobante/{codigoVenta}")
    public String mostrarComprobante(@PathVariable String codigoVenta,
                                    HttpSession session,
                                    Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        
        var venta = ventaService.buscarVentaPorCodigo(codigoVenta)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        
        model.addAttribute("venta", venta);
        return "carrito/comprobante";
    }
    
    @GetMapping("/historial")
    public String mostrarHistorialCompras(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        var historial = ventaService.obtenerHistorialCliente(cliente.getIdCliente());
        
        model.addAttribute("historial", historial);
        return "cliente/historial";
    }
}