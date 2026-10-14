package com.soa.alquilaya.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soa.alquilaya.models.OficinaModel;
import com.soa.alquilaya.repositories.IOficinaRepository;
import com.soa.alquilaya.repositories.IVehiculoRepository;

@Service
public class OficinaService {

    @Autowired
    private IOficinaRepository oficinaRepository;

    @Autowired
    private IVehiculoRepository vehiculoRepository;

    public List<OficinaModel> listar() {
        return oficinaRepository.findAll();
    }

    public OficinaModel buscarPorId(Long id) {
        return oficinaRepository.findById(id).orElse(null);
    }

    public OficinaModel guardar(OficinaModel oficina) {
        return oficinaRepository.save(oficina);
    }

    public boolean tieneVehiculos(Long id) {
        return vehiculoRepository.existsByOficinaIdOficina(id);
    }

    public void borrar(Long id) {
        oficinaRepository.deleteById(id);
    }
}
