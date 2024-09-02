package com.br.tgid.tgid.service;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.br.tgid.tgid.entity.Client;
import com.br.tgid.tgid.entity.Company;
import com.br.tgid.tgid.entity.Transaction;

public class SystemBridge {

    private List<Client> listClients = new ArrayList<>();
    private List<Company> listCompanies = new ArrayList<>();

    public void addClient(Client client) {
        if (client.checkCPF()) {
            listClients.add(client);
        }
    }

    public void addCompany(Company company) {
        if (company.checkCNPJ()) {
            listCompanies.add(company);
        }
    }

     public boolean executeTransaction(String type, double value, Company company, Client client, String date, String callbackURL, String notify) {
        Transaction transaction = new Transaction(type, value, company, client, date, callbackURL, notify);
        return transaction.run();
    }

    public List<Client> getListClients() {
        return listClients;
    }

    public List<Company> getListCompanies() {
        return listCompanies;
    }
}

