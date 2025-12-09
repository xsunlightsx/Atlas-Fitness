package com.example.ATLAS.FITNESS.service;

import com.example.ATLAS.FITNESS.model.Venta;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {
    
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }
    
    public void enviarEmailBienvenida(String toEmail, String nombreCliente) {
        try {
            Context context = new Context();
            context.setVariable("nombre", nombreCliente);
            context.setVariable("mensaje", "¡Bienvenido a Atlas Fitness! Ahora puedes disfrutar de todos nuestros productos y servicios.");
            
            String htmlContent = templateEngine.process("email/bienvenida", context);
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(toEmail);
            helper.setSubject("¡Bienvenido a Atlas Fitness!");
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar email de bienvenida", e);
        }
    }
    
    public void enviarEmailConfirmacionVenta(String toEmail, String nombreCliente, Venta venta) {
        try {
            Context context = new Context();
            context.setVariable("nombre", nombreCliente);
            context.setVariable("venta", venta);
            context.setVariable("fecha", venta.getFechaVenta()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            context.setVariable("total", venta.getTotal().setScale(2));
            
            String htmlContent = templateEngine.process("email/confirmacion-venta", context);
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(toEmail);
            helper.setSubject("Confirmación de Compra - Atlas Fitness");
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar email de confirmación", e);
        }
    }
    
    public void enviarEmailResetPassword(String toEmail, String token) {
        try {
            String resetLink = "http://localhost:8080/auth/reset-password?token=" + token;
            
            Context context = new Context();
            context.setVariable("resetLink", resetLink);
            
            String htmlContent = templateEngine.process("email/reset-password", context);
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(toEmail);
            helper.setSubject("Restablecer Contraseña - Atlas Fitness");
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar email de reset", e);
        }
    }
}