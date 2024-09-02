package com.br.tgid.tgid.service;

import com.br.tgid.tgid.entity.Client;
import com.br.tgid.tgid.entity.Company;
import com.br.tgid.tgid.entity.Email;
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

    @Autowired
    private EmailService emailService;

    @Transactional
    public boolean runTransaction(String type, double value, Long companyId, Long clientId, String date,
            String callbackURL, String notify) {
        Client client = clientService.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found."));
        Company company = companyService.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found."));

        Transaction transaction = new Transaction(type, value, company, client, date, callbackURL, notify);
        boolean result = transaction.run();

        if (result) {
            clientService.save(client);
            companyService.save(company);
        } else if (!result && type.equals("saque")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo insuficiente para o saque.");
        }

        // Send notification if requested
        if ("email".equalsIgnoreCase(notify) && client.getEmail() != null) {
            String emailBody = String.format(
                    "Dear %s,\n\nYour recent transaction was successful.\n\n" +
                            "Transaction Details:\n" +
                            "Type: %s\n" +
                            "Amount: %.2f\n" +
                            "Date: %s\n" +
                            "Updated Balance: %.2f\n\n" +
                            "Thank you for choosing our services.\n\n" +
                            "Best regards,\n" +
                            "Your Company",
                    client.getName(),
                    type,
                    value,
                    date,
                    client.getBalance());

            Email email = new Email(client.getEmail(), "Transaction Notification", emailBody);
            emailService.sendEmail(email);
        }

        return result;
    }
}
