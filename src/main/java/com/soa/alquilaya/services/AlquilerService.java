package com.soa.alquilaya.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soa.alquilaya.models.AlquilerModel;
import com.soa.alquilaya.repositories.IAlquilerRepository;

@Service
public class AlquilerService {

    @Autowired
    private IAlquilerRepository alquilerRepository;

    public List<AlquilerModel> listar() {
        return alquilerRepository.findAll();
    }

    public AlquilerModel buscarPorId(Long id) {
        return alquilerRepository.findById(id).orElse(null);
    }

    public AlquilerModel guardar(AlquilerModel alquiler) {
        return alquilerRepository.save(alquiler);
    }

    public void borrar(Long id) {
        alquilerRepository.deleteById(id);
    }
}
