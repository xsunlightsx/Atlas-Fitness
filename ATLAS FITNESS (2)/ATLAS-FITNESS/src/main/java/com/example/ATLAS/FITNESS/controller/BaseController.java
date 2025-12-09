package com.example.ATLAS.FITNESS.controller;

import com.example.ATLAS.FITNESS.model.Cliente;
import com.example.ATLAS.FITNESS.model.Usuario;
import jakarta.servlet.http.HttpSession;

public abstract class BaseController {
    
    protected Usuario getUsuario(HttpSession session) {
        return (Usuario) session.getAttribute("usuario");
    }
    
    protected Cliente getCliente(HttpSession session) {
        return (Cliente) session.getAttribute("cliente");
    }
    
    protected Long getClienteId(HttpSession session) {
        Cliente cliente = getCliente(session);
        if (cliente != null) {
            return cliente.getClienteId();
        }
        
        Usuario usuario = getUsuario(session);
        if (usuario != null && usuario.getClienteId() != null) {
            return usuario.getClienteId();
        }
        
        return null;
    }
    
    protected boolean isAuthenticated(HttpSession session) {
        return getUsuario(session) != null;
    }
    
    protected boolean isCliente(HttpSession session) {
        Usuario usuario = getUsuario(session);
        return usuario != null && Usuario.Rol.CLIENTE.equals(usuario.getRol());
    }
    
    protected boolean isAdmin(HttpSession session) {
        Usuario usuario = getUsuario(session);
        return usuario != null && usuario.esAdmin();
    }
    
    protected void requireAuth(HttpSession session) {
        if (!isAuthenticated(session)) {
            throw new SecurityException("Acceso denegado: Usuario no autenticado");
        }
    }
    
    protected void requireCliente(HttpSession session) {
        requireAuth(session);
        if (!isCliente(session)) {
            throw new SecurityException("Acceso denegado: Se requiere rol CLIENTE");
        }
    }
}