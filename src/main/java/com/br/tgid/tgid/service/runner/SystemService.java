package com.br.tgid.tgid.service.runner;

import com.br.tgid.tgid.entity.Company;
import com.br.tgid.tgid.entity.Client;
import com.br.tgid.tgid.entity.Transaction;
import com.br.tgid.tgid.repository.CompanyRepository;
import com.br.tgid.tgid.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SystemService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ClientRepository clientRepository;

    public Company saveCompany(Company company) {
        return companyRepository.save(company);
    }

    public Client saveClient(Client client) {
        return clientRepository.save(client);
    }

    public Company findCompanyById(Long id) {
        return companyRepository.findById(id).orElse(null);
    }

    public Client findClientById(Long id) {
        return clientRepository.findById(id).orElse(null);
    }

    public boolean runSystem(String type, double value, Company company, Client client, String date, String callbackURL, String notify) {
        // Criar uma transação
        Transaction transaction = new Transaction(type, value, company, client, date, callbackURL, notify);

        // Executar a transação e retornar o resultado
        return transaction.run();
    }
}
