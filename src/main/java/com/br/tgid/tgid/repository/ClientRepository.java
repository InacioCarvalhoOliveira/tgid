package com.br.tgid.tgid.repository;

import com.br.tgid.tgid.entity.Client;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
        Optional<Client> findByCpf(String cpf);

}

