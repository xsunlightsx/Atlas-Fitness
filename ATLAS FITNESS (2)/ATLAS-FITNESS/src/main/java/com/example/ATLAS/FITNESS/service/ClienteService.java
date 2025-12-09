package com.example.ATLAS.FITNESS.service;

import com.example.ATLAS.FITNESS.model.Cliente;
import com.example.ATLAS.FITNESS.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return clienteRepository.findByUsuarioUsername(username);
    }
    
    public Optional<Cliente> buscarPorDni(String dni) {
        return clienteRepository.findByDni(dni);
    }
    
    public List<Cliente> listarClientesActivos() {
        return clienteRepository.findByEstado(Cliente.Estado.ACTIVO);
    }
    
    @Transactional
    public Cliente actualizarPerfil(Long idCliente, Cliente datosActualizados) {
        return clienteRepository.findById(idCliente).map(cliente -> {
            cliente.setDireccion(datosActualizados.getDireccion());
            cliente.setTelefono(datosActualizados.getTelefono());
            cliente.setEmail(datosActualizados.getEmail());
            cliente.setPeso(datosActualizados.getPeso());
            cliente.setAltura(datosActualizados.getAltura());
            cliente.setObjetivo(datosActualizados.getObjetivo());
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