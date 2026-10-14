package com.soa.alquilaya.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soa.alquilaya.models.OficinaModel;

public interface IOficinaRepository extends JpaRepository<OficinaModel, Long> {
}
