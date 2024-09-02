package com.br.tgid.tgid.service;

import com.br.tgid.tgid.entity.Client;
import com.br.tgid.tgid.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public Client createClient(Client client) {
        // Validate CPF
        if (!client.checkCPF()) {
            throw new IllegalArgumentException("CPF inválido.");
        }

        // Check if the client with the same CPF already exists
        Optional<Client> existingClient = clientRepository.findByCpf(client.getCpf());
        if (existingClient.isPresent()) {
            throw new IllegalArgumentException("Já existe um cliente com este CPF.");
        }

        // Save the client if the CPF is valid and not a duplicate
        return clientRepository.save(client);
    }

    public Client updateClient(Long id, Client client) {
        // Validate CPF
        if (!client.checkCPF()) {
            throw new IllegalArgumentException("CPF inválido.");
        }

        // Update client information
        client.setId(id);
        return clientRepository.save(client);
    }

    public Client save(Client client) {
        return clientRepository.save(client);
    }

    public Optional<Client> findById(Long id) {
        return clientRepository.findById(id);
    }

    public void deleteById(Long id) {
        clientRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return clientRepository.existsById(id);
    }
}
