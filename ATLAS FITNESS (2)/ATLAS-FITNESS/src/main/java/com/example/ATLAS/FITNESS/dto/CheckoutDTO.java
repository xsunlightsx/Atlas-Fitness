package com.example.ATLAS.FITNESS.dto;

import jakarta.validation.constraints.NotBlank;

public class CheckoutDTO {
    
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    @NotBlank(message = "El email es obligatorio")
    private String email;
    
    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;
    
    private String direccion;
    private String ciudad;
    private String distrito;
    
    @NotBlank(message = "El método de pago es obligatorio")
    private String metodoPago;
    
    private boolean usarDatosCliente = true;
    private String observaciones;
    
    // Para tarjeta de crédito
    private String numeroTarjeta;
    private String fechaExpiracion;
    private String cvv;
    private String nombreTitular;
    
    // Constructor por defecto
    public CheckoutDTO() {}
    
    // Constructor con parámetros principales
    public CheckoutDTO(String nombre, String email, String telefono, String metodoPago) {
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.metodoPago = metodoPago;
    }
    
    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    
    public String getDistrito() { return distrito; }
    public void setDistrito(String distrito) { this.distrito = distrito; }
    
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    
    public boolean isUsarDatosCliente() { return usarDatosCliente; }
    public void setUsarDatosCliente(boolean usarDatosCliente) { this.usarDatosCliente = usarDatosCliente; }
    
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    
    public String getNumeroTarjeta() { return numeroTarjeta; }
    public void setNumeroTarjeta(String numeroTarjeta) { this.numeroTarjeta = numeroTarjeta; }
    
    public String getFechaExpiracion() { return fechaExpiracion; }
    public void setFechaExpiracion(String fechaExpiracion) { this.fechaExpiracion = fechaExpiracion; }
    
    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }
    
    public String getNombreTitular() { return nombreTitular; }
    public void setNombreTitular(String nombreTitular) { this.nombreTitular = nombreTitular; }
    
    // Métodos auxiliares
    public boolean tieneDatosTarjeta() {
        return metodoPago != null && 
               (metodoPago.equals("TARJETA_CREDITO") || metodoPago.equals("TARJETA_DEBITO")) &&
               numeroTarjeta != null && !numeroTarjeta.isEmpty() &&
               fechaExpiracion != null && !fechaExpiracion.isEmpty() &&
               cvv != null && !cvv.isEmpty();
    }
    
    // toString() para debugging
    @Override
    public String toString() {
        return "CheckoutDTO{" +
                "nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", telefono='" + telefono + '\'' +
                ", metodoPago='" + metodoPago + '\'' +
                ", usarDatosCliente=" + usarDatosCliente +
                '}';
    }
}