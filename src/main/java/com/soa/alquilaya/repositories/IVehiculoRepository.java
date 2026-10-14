package com.soa.alquilaya.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soa.alquilaya.models.VehiculoModel;

public interface IVehiculoRepository extends JpaRepository<VehiculoModel, Long> {

    VehiculoModel findByMatricula(String matricula);

    List<VehiculoModel> findByEstado(String estado);

    boolean existsByOficinaIdOficina(Long idOficina);
}
