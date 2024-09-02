package com.br.tgid.tgid.service;

import com.br.tgid.tgid.entity.Client;
import com.br.tgid.tgid.entity.Company;
import com.br.tgid.tgid.entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TransactionService {

    @Autowired
    private ClientService clientService;

    @Autowired
    private CompanyService companyService;

    @Transactional
    public boolean runTransaction(String type, double value, Long companyId, Long clientId, String date,
            String callbackURL, String notify) {
        // Encontre o cliente e a empresa pelo ID, lançando exceções se não encontrados
        Client client = clientService.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found."));
        Company company = companyService.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found."));

        // Crie uma nova transação e execute-a
        Transaction transaction = new Transaction(type, value, company, client, date, callbackURL, notify);
        boolean result = transaction.run();

        if (result) {
            // Persista as mudanças no cliente e na empresa
            clientService.save(client);
            companyService.save(company);
        } else if (!result && type.equals("saque")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo insuficiente para o saque.");
        } 

        return result;
    }
}
