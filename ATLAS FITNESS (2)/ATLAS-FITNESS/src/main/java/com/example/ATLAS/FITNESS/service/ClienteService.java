package com.example.ATLAS.FITNESS.service;

import com.example.ATLAS.FITNESS.model.Cliente;
import com.example.ATLAS.FITNESS.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {
    
    private final ClienteRepository clienteRepository;
    
    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }
    
    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }
    
    public Optional<Cliente> buscarPorUsername(String username) {
        return clienteRepository.buscarPorUsername(username);
    }
    
    public Optional<Cliente> buscarPorEmail(String email) {
        return clienteRepository.findByEmail(email);
    }
    
    public Optional<Cliente> buscarPorDni(String dni) {
        return clienteRepository.findByDni(dni);
    }
    
    public List<Cliente> listarClientesActivos() {
        return clienteRepository.findByEstado(Cliente.Estado.ACTIVO);
    }
    
    @Transactional
    public Cliente actualizarPerfil(Cliente clienteActualizado) {
        return clienteRepository.save(clienteActualizado);
    }
    
    @Transactional
    public Cliente actualizarPerfil(Long idCliente, Cliente datosActualizados) {
        return clienteRepository.findById(idCliente).map(cliente -> {
            // Solo actualizar campos permitidos
            cliente.setTelefono(datosActualizados.getTelefono());
            cliente.setDireccion(datosActualizados.getDireccion());
            cliente.setFechaNacimiento(datosActualizados.getFechaNacimiento());
            cliente.setGenero(datosActualizados.getGenero());
            
            // Manejar BigDecimal correctamente
            if (datosActualizados.getAltura() != null) {
                cliente.setAltura(datosActualizados.getAltura());
            }
            
            if (datosActualizados.getPeso() != null) {
                cliente.setPeso(datosActualizados.getPeso());
            }
            
            cliente.setObjetivo(datosActualizados.getObjetivo());
            // NO actualizar: dni, nombre, apellido, email, estado
            
            return clienteRepository.save(cliente);
        }).orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }
    
    // Método sobrecargado para compatibilidad con Double
    @Transactional
    public Cliente actualizarPerfil(Long idCliente, Double altura, Double peso, String objetivo) {
        return clienteRepository.findById(idCliente).map(cliente -> {
            if (altura != null) {
                cliente.setAltura(BigDecimal.valueOf(altura));
            }
            
            if (peso != null) {
                cliente.setPeso(BigDecimal.valueOf(peso));
            }
            
            if (objetivo != null) {
                cliente.setObjetivo(objetivo);
            }
            
            return clienteRepository.save(cliente);
        }).orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }
    
    @Transactional
    public void actualizarUltimaVisita(Long idCliente) {
        clienteRepository.findById(idCliente).ifPresent(cliente -> {
            cliente.setFechaUltimaVisita(LocalDateTime.now());
            clienteRepository.save(cliente);
        });
    }
    
    public Long contarClientesActivos() {
        return clienteRepository.countClientesActivos();
    }
}