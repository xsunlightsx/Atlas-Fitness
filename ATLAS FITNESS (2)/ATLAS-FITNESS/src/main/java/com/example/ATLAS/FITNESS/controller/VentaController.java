package com.example.ATLAS.FITNESS.controller;

import com.example.ATLAS.FITNESS.dto.CheckoutDTO;
import com.example.ATLAS.FITNESS.model.Cliente;
import com.example.ATLAS.FITNESS.model.Venta;
import com.example.ATLAS.FITNESS.service.VentaService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;

@Controller
@RequestMapping("/checkout")
public class VentaController extends BaseController {
    
    private final VentaService ventaService;
    
    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }
    
    @GetMapping
    public String mostrarCheckout(HttpSession session, Model model) {
        try {
            requireCliente(session);
        } catch (SecurityException e) {
            return "redirect:/auth/login?redirect=/checkout";
        }
        
        Cliente cliente = getCliente(session);
        if (cliente == null) {
            return "redirect:/auth/login";
        }
        
        // Crear DTO con datos del cliente
        CheckoutDTO checkoutDTO = new CheckoutDTO();
        checkoutDTO.setNombre(cliente.getNombreCompleto());
        checkoutDTO.setEmail(cliente.getEmail());
        checkoutDTO.setTelefono(cliente.getTelefono());
        checkoutDTO.setDireccion(cliente.getDireccion());
        
        // Pasar métodos de pago disponibles
        model.addAttribute("checkoutDTO", checkoutDTO);
        model.addAttribute("cliente", cliente);
        model.addAttribute("metodosPago", Venta.MetodoPago.values());
        
        return "carrito/checkout";
    }
    
    @PostMapping("/procesar")
    public String procesarCheckout(@ModelAttribute @Valid CheckoutDTO checkoutDTO,
                                  BindingResult result,
                                  HttpSession session,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        try {
            requireCliente(session);
        } catch (SecurityException e) {
            return "redirect:/auth/login";
        }
        
        if (result.hasErrors()) {
            Cliente cliente = getCliente(session);
            model.addAttribute("cliente", cliente);
            model.addAttribute("metodosPago", Venta.MetodoPago.values());
            return "carrito/checkout";
        }
        
        Cliente cliente = getCliente(session);
        if (cliente == null) {
            return "redirect:/auth/login";
        }
        
        try {
            // Validar que el método de pago sea válido
            Venta.MetodoPago metodoPago = Venta.MetodoPago.valueOf(checkoutDTO.getMetodoPago());
            
            var venta = ventaService.procesarVenta(cliente.getClienteId(), checkoutDTO);
            redirectAttributes.addFlashAttribute("success", 
                "¡Compra realizada exitosamente! Código: " + venta.getCodigoVenta());
            return "redirect:/checkout/comprobante/" + venta.getCodigoVenta();
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Método de pago inválido. Opciones válidas: " + 
                Arrays.toString(Venta.MetodoPago.values()));
            model.addAttribute("cliente", cliente);
            model.addAttribute("metodosPago", Venta.MetodoPago.values());
            return "carrito/checkout";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("cliente", cliente);
            model.addAttribute("metodosPago", Venta.MetodoPago.values());
            return "carrito/checkout";
        }
    }
    
    @GetMapping("/comprobante/{codigoVenta}")
    public String mostrarComprobante(@PathVariable String codigoVenta,
                                    HttpSession session,
                                    Model model) {
        try {
            requireCliente(session);
        } catch (SecurityException e) {
            return "redirect:/auth/login";
        }
        
        Cliente cliente = getCliente(session);
        if (cliente == null) {
            return "redirect:/auth/login";
        }
        
        var venta = ventaService.buscarVentaPorCodigo(codigoVenta)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        
        // VERIFICAR QUE LA VENTA PERTENEZCA AL CLIENTE
        if (!venta.getCliente().getClienteId().equals(cliente.getClienteId())) {
            throw new RuntimeException("Acceso denegado");
        }
        
        model.addAttribute("venta", venta);
        return "carrito/comprobante";
    }
    
    @GetMapping("/historial")
    public String mostrarHistorialCompras(HttpSession session, Model model) {
        try {
            requireCliente(session);
        } catch (SecurityException e) {
            return "redirect:/auth/login";
        }
        
        Cliente cliente = getCliente(session);
        if (cliente == null) {
            return "redirect:/auth/login";
        }
        
        var historial = ventaService.obtenerHistorialCliente(cliente.getClienteId());
        
        model.addAttribute("historial", historial);
        return "cliente/historial";
    }
    
    @GetMapping("/reimprimir/{codigoVenta}")
    public String reimprimirComprobante(@PathVariable String codigoVenta,
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
        
        var venta = ventaService.buscarVentaPorCodigo(codigoVenta)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        
        // Verificar propiedad
        if (!venta.getCliente().getClienteId().equals(cliente.getClienteId())) {
            redirectAttributes.addFlashAttribute("error", "Acceso denegado");
            return "redirect:/checkout/historial";
        }
        
        redirectAttributes.addFlashAttribute("venta", venta);
        redirectAttributes.addFlashAttribute("info", "Comprobante reimpreso");
        return "redirect:/checkout/comprobante/" + codigoVenta;
    }
    
    @PostMapping("/cancelar/{codigoVenta}")
    public String cancelarVenta(@PathVariable String codigoVenta,
                               @RequestParam String motivo,
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
            var venta = ventaService.buscarVentaPorCodigo(codigoVenta)
                    .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
            
            // Verificar propiedad
            if (!venta.getCliente().getClienteId().equals(cliente.getClienteId())) {
                redirectAttributes.addFlashAttribute("error", "Acceso denegado");
                return "redirect:/checkout/historial";
            }
            
            // Verificar que no esté ya cancelada
            if (venta.getEstado() == Venta.Estado.CANCELADA) {
                redirectAttributes.addFlashAttribute("error", "La venta ya está cancelada");
                return "redirect:/checkout/historial";
            }
            
            // Aquí deberías tener un método en el servicio para cancelar ventas
            // ventaService.cancelarVenta(codigoVenta, motivo);
            
            redirectAttributes.addFlashAttribute("success", 
                "Solicitud de cancelación enviada. Nos contactaremos contigo.");
            
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/checkout/historial";
    }
}