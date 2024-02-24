package com.minhapi.parkapi.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.minhapi.parkapi.entity.Cliente;
import com.minhapi.parkapi.repository.projection.ClienteProjection;

public interface ClienteRepository extends JpaRepository<Cliente, Long>{

    @Query("select c from Cliente c")
    public Page<ClienteProjection> findAllPageable(Pageable page);

    public Cliente findByUsuarioId(Long id);

    public Optional<Cliente> findByCpf(String cpf);
    
}
