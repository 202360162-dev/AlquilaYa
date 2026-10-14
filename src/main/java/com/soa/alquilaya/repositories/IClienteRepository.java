package com.soa.alquilaya.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soa.alquilaya.models.ClienteModel;

public interface IClienteRepository extends JpaRepository<ClienteModel, Long> {

    ClienteModel findByEmail(String email);

    ClienteModel findByLicenciaConducir(String licenciaConducir);
}
