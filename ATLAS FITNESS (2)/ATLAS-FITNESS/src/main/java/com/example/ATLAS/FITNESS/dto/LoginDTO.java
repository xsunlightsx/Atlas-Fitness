package com.example.ATLAS.FITNESS.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginDTO {
    
    @NotBlank(message = "El username es requerido")
    private String username;
    
    @NotBlank(message = "La contraseña es requerida")
    private String password;
    
    // Getters y Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}