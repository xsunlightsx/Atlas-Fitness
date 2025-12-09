package com.example.ATLAS.FITNESS.service;

import com.example.ATLAS.FITNESS.model.Venta;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    // Constructor sin parámetros o sin JavaMailSender
    public EmailService() {
        // Constructor vacío
    }
    
    public void enviarEmailConfirmacionVenta(String emailDestino, String nombreCliente, Venta venta) {
        // Simular envío de email (solo logs)
        System.out.println("==========================================");
        System.out.println("SIMULACIÓN DE ENVÍO DE EMAIL");
        System.out.println("==========================================");
        System.out.println("Para: " + emailDestino);
        System.out.println("Asunto: Confirmación de compra - " + venta.getCodigoVenta());
        System.out.println("Cliente: " + nombreCliente);
        System.out.println("Total: S/ " + venta.getTotal());
        System.out.println("Método de pago: " + venta.getMetodoPago());
        System.out.println("==========================================");
        
        // En producción, aquí iría el código real para enviar emails
        // Usando JavaMailSender
    }
    
    public void enviarEmailBienvenida(String email, String nombre) {
        System.out.println("Email de bienvenida enviado a: " + email + " - " + nombre);
    }
    
    public void enviarEmailResetPassword(String email, String token) {
        System.out.println("Email de reset password enviado a: " + email);
        System.out.println("Token: " + token);
    }
}