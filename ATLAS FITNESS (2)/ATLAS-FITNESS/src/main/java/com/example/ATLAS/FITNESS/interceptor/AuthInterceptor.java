package com.example.ATLAS.FITNESS.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, 
                           HttpServletResponse response, 
                           Object handler) throws Exception {
        
        String requestURI = request.getRequestURI();
        
        // Rutas públicas
        if (requestURI.startsWith("/auth/") || 
            requestURI.startsWith("/css/") || 
            requestURI.startsWith("/js/") || 
            requestURI.startsWith("/images/") || 
            requestURI.equals("/") || 
            requestURI.equals("/index")) {
            return true;
        }
        
        // Verificar si el usuario está autenticado por Spring Security
        boolean isAuthenticated = SecurityContextHolder.getContext()
                .getAuthentication() != null && 
                SecurityContextHolder.getContext()
                .getAuthentication()
                .isAuthenticated();
        
        // Verificar que no sea el usuario anónimo
        if (isAuthenticated) {
            String username = SecurityContextHolder.getContext()
                    .getAuthentication().getName();
            if ("anonymousUser".equals(username)) {
                isAuthenticated = false;
            }
        }
        
        // Si no está autenticado, guardar la URL
        if (!isAuthenticated) {
            HttpSession session = request.getSession(true);
            session.setAttribute("url_prior_login", requestURI);
        }
        
        return true;
    }
}