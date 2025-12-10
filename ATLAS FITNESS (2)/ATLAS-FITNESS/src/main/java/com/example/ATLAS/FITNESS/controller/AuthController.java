package com.example.ATLAS.FITNESS.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {
    
    @GetMapping("/login")
    public String loginPage(Authentication authentication) {
        // Si el usuario YA está autenticado, redirigir según su rol
        if (authentication != null && authentication.isAuthenticated() 
            && !"anonymousUser".equals(authentication.getName())) {
            
            boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_ADMIN"));
            
            if (isAdmin) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/cliente/dashboard";
            }
        }
        
        // Si NO está autenticado, mostrar la página de login
        return "auth/login";
    }
}