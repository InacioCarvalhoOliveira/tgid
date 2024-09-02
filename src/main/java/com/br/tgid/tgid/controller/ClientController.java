package com.br.tgid.tgid.controller;

import com.br.tgid.tgid.entity.Client;
import com.br.tgid.tgid.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @PostMapping
    public Client createClient(@RequestBody Client client) {
        // Delegates to the service layer to handle the creation and validation
        return clientService.createClient(client);
    }

    @GetMapping("/{id}")
    public Optional<Client> getClient(@PathVariable Long id) {
        // Delegates to the service layer to fetch the client by ID
        return clientService.findById(id);
    }

    @PutMapping("/{id}")
    public Client updateClient(@PathVariable Long id, @RequestBody Client client) {
        // Delegates to the service layer to handle the update and validation
        return clientService.updateClient(id, client);
    }

    @DeleteMapping("/{id}")
    public void deleteClient(@PathVariable Long id) {
        // Delegates to the service layer to handle deletion
        clientService.deleteById(id);
    }
}
