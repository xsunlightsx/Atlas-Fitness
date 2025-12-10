package com.example.ATLAS.FITNESS;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerateBCryptHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Hash VÁLIDO para "123"
        String hash123 = encoder.encode("123");
        System.out.println("=== HASH BCRYPT VÁLIDO PARA '123' ===");
        System.out.println(hash123);
        System.out.println("Longitud: " + hash123.length());
        
        // Verificar que es válido
        System.out.println("Es válido: " + encoder.matches("123", hash123));
    }
}