package com.example.ATLAS.FITNESS.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginDTO {
    
    @NotBlank(message = "El username es obligatorio")
    private String username;
    
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
    
    private boolean rememberMe;
    
    // Constructor por defecto
    public LoginDTO() {}
    
    // Constructor con parámetros
    public LoginDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    // Getters y Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public boolean isRememberMe() { return rememberMe; }
    public void setRememberMe(boolean rememberMe) { this.rememberMe = rememberMe; }
    
    // toString() para debugging
    @Override
    public String toString() {
        return "LoginDTO{" +
                "username='" + username + '\'' +
                ", rememberMe=" + rememberMe +
                '}';
    }
}