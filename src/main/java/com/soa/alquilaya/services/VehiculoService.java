package com.soa.alquilaya.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soa.alquilaya.models.VehiculoModel;
import com.soa.alquilaya.repositories.IAlquilerRepository;
import com.soa.alquilaya.repositories.IVehiculoRepository;

@Service
public class VehiculoService {

    @Autowired
    private IVehiculoRepository vehiculoRepository;

    @Autowired
    private IAlquilerRepository alquilerRepository;

    public List<VehiculoModel> listar() {
        return vehiculoRepository.findAll();
    }

    public VehiculoModel buscarPorId(Long id) {
        return vehiculoRepository.findById(id).orElse(null);
    }

    public VehiculoModel buscarPorMatricula(String matricula) {
        return vehiculoRepository.findByMatricula(matricula);
    }

    public List<VehiculoModel> buscarPorEstado(String estado) {
        return vehiculoRepository.findByEstado(estado.toUpperCase());
    }

    public VehiculoModel guardar(VehiculoModel vehiculo) {
        if (vehiculo.getEstado() == null) {
            vehiculo.setEstado("DISPONIBLE");
        }
        return vehiculoRepository.save(vehiculo);
    }

    public void cambiarEstado(VehiculoModel vehiculo, String estado) {
        vehiculo.setEstado(estado);
        vehiculoRepository.save(vehiculo);
    }

    public boolean tieneAlquileres(Long id) {
        return alquilerRepository.existsByVehiculoIdVehiculo(id);
    }

    public void borrar(Long id) {
        vehiculoRepository.deleteById(id);
    }
}
