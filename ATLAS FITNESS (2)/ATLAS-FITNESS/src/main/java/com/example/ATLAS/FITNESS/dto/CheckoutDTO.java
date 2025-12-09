package com.example.ATLAS.FITNESS.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class CheckoutDTO {
    
    @NotBlank(message = "El nombre es requerido")
    private String nombre;
    
    @NotBlank(message = "El email es requerido")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Email inválido")
    private String email;
    
    @NotBlank(message = "El teléfono es requerido")
    @Pattern(regexp = "^[0-9]{9,15}$", message = "Teléfono inválido")
    private String telefono;
    
    @NotBlank(message = "La dirección es requerida")
    private String direccion;
    
    @NotBlank(message = "El método de pago es requerido")
    private String metodoPago;
    
    private String observaciones;
    
    @NotNull(message = "Debe aceptar los términos y condiciones")
    private Boolean aceptaTerminos = false;
    
    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    
    public Boolean getAceptaTerminos() { return aceptaTerminos; }
    public void setAceptaTerminos(Boolean aceptaTerminos) { this.aceptaTerminos = aceptaTerminos; }
}