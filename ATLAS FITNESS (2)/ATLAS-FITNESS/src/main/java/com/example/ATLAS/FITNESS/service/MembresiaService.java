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
        return membresiaRepository.findByClienteIdCliente(idCliente);
    }
    
    @Transactional
    public Membresia crearMembresia(Cliente cliente, Membresia.TipoMembresia tipo, BigDecimal precio) {
        // Desactivar membresías anteriores
        List<Membresia> membresiasAnteriores = membresiaRepository.findByClienteIdCliente(cliente.getIdCliente());
        membresiasAnteriores.forEach(m -> m.setEstado(Membresia.Estado.VENCIDA));
        membresiaRepository.saveAll(membresiasAnteriores);
        
        // Crear nueva membresía
        Membresia nuevaMembresia = new Membresia(cliente, tipo, LocalDate.now(), precio);
        nuevaMembresia.setEstado(Membresia.Estado.ACTIVA);
        nuevaMembresia.setFechaRegistro(LocalDateTime.now());
        
        return membresiaRepository.save(nuevaMembresia);
    }
    
    @Transactional
    public Membresia renovarMembresia(Long idMembresia) {
        return membresiaRepository.findById(idMembresia).map(membresia -> {
            if (!membresia.estaActiva()) {
                throw new RuntimeException("La membresía no está activa");
            }
            
            // Calcular nueva fecha de fin según el tipo
            LocalDate nuevaFechaFin = switch (membresia.getTipo()) {
                case MENSUAL -> membresia.getFechaFin().plusMonths(1);
                case TRIMESTRAL -> membresia.getFechaFin().plusMonths(3);
                case SEMESTRAL -> membresia.getFechaFin().plusMonths(6);
                case ANUAL -> membresia.getFechaFin().plusYears(1);
            };
            
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
}