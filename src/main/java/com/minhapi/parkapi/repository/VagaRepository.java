package com.minhapi.parkapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.minhapi.parkapi.entity.Vaga;

public interface VagaRepository extends JpaRepository<Vaga, Long> {

    Optional<Vaga> findByCodigo(String codigo);

    Optional<Vaga> findFirstByStatus(Vaga.StatusVaga statusVaga);
    
}
