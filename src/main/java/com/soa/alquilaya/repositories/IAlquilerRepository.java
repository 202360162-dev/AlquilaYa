package com.soa.alquilaya.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soa.alquilaya.models.AlquilerModel;

public interface IAlquilerRepository extends JpaRepository<AlquilerModel, Long> {

    boolean existsByClienteIdCliente(Long idCliente);

    boolean existsByVehiculoIdVehiculo(Long idVehiculo);
}
