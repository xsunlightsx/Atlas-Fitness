package com.example.ATLAS.FITNESS.service;

import com.example.ATLAS.FITNESS.model.Cliente;
import com.example.ATLAS.FITNESS.model.Membresia;
import com.example.ATLAS.FITNESS.repository.MembresiaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MembresiaService {
    
    private final MembresiaRepository membresiaRepository;
    
    public MembresiaService(MembresiaRepository membresiaRepository) {
        this.membresiaRepository = membresiaRepository;
    }
    
    public Optional<Membresia> buscarMembresiaActivaCliente(Long idCliente) {
        return membresiaRepository.findMembresiaActivaByCliente(idCliente);
    }
    
    public List<Membresia> obtenerMembresiasCliente(Long idCliente) {
        return membresiaRepository.findByClienteClienteId(idCliente);
    }
    
    @Transactional
    public Membresia crearMembresia(Cliente cliente, Membresia.TipoMembresia tipo, BigDecimal precio) {
        // Desactivar membresías anteriores
        List<Membresia> membresiasAnteriores = membresiaRepository.findByClienteClienteId(cliente.getClienteId());
        if (membresiasAnteriores != null && !membresiasAnteriores.isEmpty()) {
            membresiasAnteriores.forEach(m -> m.setEstado(Membresia.Estado.VENCIDA));
            membresiaRepository.saveAll(membresiasAnteriores);
        }
        
        // Crear nueva membresía usando el constructor
        LocalDate fechaInicio = LocalDate.now();
        Membresia nuevaMembresia = new Membresia(cliente, tipo, fechaInicio, precio);
        
        // Asegurar que se calcule la fecha fin
        if (nuevaMembresia.getFechaFin() == null) {
            nuevaMembresia.setFechaFin(Membresia.calcularFechaFin(tipo, fechaInicio));
        }
        
        nuevaMembresia.setEstado(Membresia.Estado.ACTIVA);
        nuevaMembresia.setFechaRegistro(LocalDateTime.now());
        
        return membresiaRepository.save(nuevaMembresia);
    }
    
    @Transactional
    public Membresia renovarMembresia(Long idMembresia) {
        return membresiaRepository.findById(idMembresia).map(membresia -> {
            // Verificar si la membresía está activa y no vencida
            if (!membresia.estaActiva()) {
                throw new RuntimeException("La membresía no está activa");
            }
            
            if (membresia.estaVencida()) {
                throw new RuntimeException("La membresía está vencida. No se puede renovar.");
            }
            
            // Si la fecha fin es null, usar fecha actual
            LocalDate fechaBase = (membresia.getFechaFin() != null) ? 
                                 membresia.getFechaFin() : LocalDate.now();
            
            // Calcular nueva fecha de fin según el tipo
            LocalDate nuevaFechaFin = Membresia.calcularFechaFin(membresia.getTipo(), fechaBase);
            
            membresia.setFechaFin(nuevaFechaFin);
            membresia.setFechaRenovacion(LocalDateTime.now());
            
            return membresiaRepository.save(membresia);
        }).orElseThrow(() -> new RuntimeException("Membresía no encontrada"));
    }
    
    @Transactional
    public void registrarAsistencia(Long idCliente) {
        membresiaRepository.findMembresiaActivaByCliente(idCliente)
                .ifPresent(membresia -> {
                    membresia.incrementarAsistencia();
                    membresiaRepository.save(membresia);
                });
    }
    
    @Transactional
    public void cancelarMembresia(Long idMembresia) {
        membresiaRepository.findById(idMembresia).ifPresent(membresia -> {
            membresia.setEstado(Membresia.Estado.CANCELADA);
            membresiaRepository.save(membresia);
        });
    }
    
    public List<Membresia> obtenerMembresiasPorVencer() {
        LocalDate hoyMas15Dias = LocalDate.now().plusDays(15);
        return membresiaRepository.findMembresiasPorVencer(hoyMas15Dias);
    }
    
    public Long contarMembresiasActivas() {
        return membresiaRepository.countMembresiasActivas();
    }
    
    // Método adicional para buscar membresía por código
    public Optional<Membresia> buscarPorCodigo(String codigoMembresia) {
        return membresiaRepository.findByCodigoMembresia(codigoMembresia);
    }
    
    // Método para verificar si un cliente tiene membresía activa
    public boolean tieneMembresiaActiva(Long idCliente) {
        return membresiaRepository.findMembresiaActivaByCliente(idCliente).isPresent();
    }
    
    // Método para obtener membresía vigente (activa y dentro de fechas)
    public Optional<Membresia> obtenerMembresiaVigente(Long idCliente) {
        return membresiaRepository.findMembresiaVigenteByCliente(idCliente);
    }
}