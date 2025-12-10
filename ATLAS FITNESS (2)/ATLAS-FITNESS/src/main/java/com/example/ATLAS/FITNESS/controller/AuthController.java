package com.example.ATLAS.FITNESS.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth")
public class AuthController {
    
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                       @RequestParam(value = "logout", required = false) String logout,
                       @RequestParam(value = "accessDenied", required = false) String accessDenied,
                       Model model) {
        
        if (error != null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
        }
        if (logout != null) {
            model.addAttribute("message", "Has cerrado sesión exitosamente");
        }
        if (accessDenied != null) {
            model.addAttribute("error", "No tienes permiso para acceder a esta página");
        }
        
        return "auth/login";
    }
    
    @GetMapping("/registro")
    public String registro() {
        return "auth/registro";
    }
    
    @GetMapping("/olvido-password")
    public String olvidoPassword() {
        return "auth/olvido-password";
    }
}