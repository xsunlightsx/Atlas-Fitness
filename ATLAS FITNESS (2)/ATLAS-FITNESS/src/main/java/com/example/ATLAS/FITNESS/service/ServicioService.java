package com.example.ATLAS.FITNESS.service;

import com.example.ATLAS.FITNESS.model.Servicio;
import com.example.ATLAS.FITNESS.repository.ServicioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServicioService {
    
    private final ServicioRepository servicioRepository;
    
    public ServicioService(ServicioRepository servicioRepository) {
        this.servicioRepository = servicioRepository;
    }
    
    public List<Servicio> listarServiciosDisponibles() {
        return servicioRepository.findServiciosDisponibles();
    }
    
    public List<Servicio> buscarServicios(String query) {
        return servicioRepository.buscarServicios(query);
    }
    
    public List<Servicio> listarPorTipo(Servicio.TipoServicio tipo) {
        return servicioRepository.findByTipo(tipo);
    }
    
    public Optional<Servicio> buscarPorId(Long id) {
        return servicioRepository.findById(id);
    }
    
    public Optional<Servicio> buscarPorCodigo(String codigo) {
        return servicioRepository.findByCodigo(codigo);
    }
    
    public List<Servicio> listarEntrenamientosPersonales() {
        return servicioRepository.findByTipo(Servicio.TipoServicio.ENTRENAMIENTO_PERSONAL)
                .stream()
                .filter(Servicio::estaDisponible)
                .toList();
    }
    
    public List<Servicio> listarAsesoriasNutricionales() {
        return servicioRepository.findByTipo(Servicio.TipoServicio.ASESORIA_NUTRICIONAL)
                .stream()
                .filter(Servicio::estaDisponible)
                .toList();
    }
    
    public List<Servicio> listarClasesGrupales() {
        return servicioRepository.findByTipo(Servicio.TipoServicio.CLASE_GRUPAL)
                .stream()
                .filter(Servicio::estaDisponible)
                .toList();
    }
}