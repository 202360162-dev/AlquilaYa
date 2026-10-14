package com.soa.alquilaya.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soa.alquilaya.models.ClienteModel;
import com.soa.alquilaya.repositories.IAlquilerRepository;
import com.soa.alquilaya.repositories.IClienteRepository;

@Service
public class ClienteService {

    @Autowired
    private IClienteRepository clienteRepository;

    @Autowired
    private IAlquilerRepository alquilerRepository;

    public List<ClienteModel> listar() {
        return clienteRepository.findAll();
    }

    public ClienteModel buscarPorId(Long id) {
        return clienteRepository.findById(id).orElse(null);
    }

    public ClienteModel buscarPorEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    public ClienteModel buscarPorLicencia(String licencia) {
        return clienteRepository.findByLicenciaConducir(licencia);
    }

    public ClienteModel guardar(ClienteModel cliente) {
        return clienteRepository.save(cliente);
    }

    public boolean tieneAlquileres(Long id) {
        return alquilerRepository.existsByClienteIdCliente(id);
    }

    public void borrar(Long id) {
        clienteRepository.deleteById(id);
    }
}
