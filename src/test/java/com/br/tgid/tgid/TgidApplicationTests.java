package com.br.tgid.tgid;

import com.br.tgid.tgid.service.ClientService;
import com.br.tgid.tgid.service.CompanyService;
import com.br.tgid.tgid.service.EmailService;
import com.br.tgid.tgid.service.TransactionService;
import com.br.tgid.tgid.entity.Client;
import com.br.tgid.tgid.entity.Company;
import com.br.tgid.tgid.entity.Email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

@SpringBootTest
class TgidApplicationTests {

    @Autowired
    private TransactionService transactionService;

    @MockBean
    private ClientService clientService;

    @MockBean
    private CompanyService companyService;

    @MockBean
    private EmailService emailService; // Mock EmailService

    private Client testClient;
    private Company testCompany;

    @BeforeEach
    void setUp() {
        testClient = new Client();
        testClient.setId(1L);
        testClient.setBalance(200.00);
        testClient.setCpf("763.310.890-80");
        testClient.setEmail("setup@setup.com");

        testCompany = new Company();
        testCompany.setId(8L);
        testCompany.setBalance(1000.00);
        testCompany.setSystemTax(0.05);

        // Mock services
        when(clientService.findById(1L)).thenReturn(Optional.of(testClient));
        when(companyService.findById(8L)).thenReturn(Optional.of(testCompany));
    }

    @Test
    @Transactional
    void testSuccessfulDeposit() {
        double depositAmount = 100.00;
        double initialCompanyBalance = testCompany.getBalance();

        transactionService.runTransaction("deposito", depositAmount, 8L, 1L, "01/09/2024", null, "email");

        double updatedCompanyBalance = companyService.findById(8L).get().getBalance();
        assertEquals(initialCompanyBalance + depositAmount, updatedCompanyBalance,
                "O saldo da empresa não foi atualizado corretamente após o depósito.");

        // Verify email sending
        ArgumentCaptor<Email> emailCaptor = ArgumentCaptor.forClass(Email.class);
        verify(emailService, times(1)).sendEmail(emailCaptor.capture());

        Email sentEmail = emailCaptor.getValue();
        assertEquals("setup@setup.com", sentEmail.getTo());
        assertEquals("Transaction Notification", sentEmail.getSubject());
        String expectedEmailBody = String.format(
                "Dear %s,\n\n" +
                        "Your recent transaction was successful.\n\n" +
                        "Transaction Details:\n" +
                        "Type: %s\n" +
                        "Amount: %.2f\n" +
                        "Date: %s\n" +
                        "Updated Balance: %.2f\n\n" +
                        "Thank you for choosing our services.\n\n" +
                        "Best regards,\n" +
                        "Your Company",
                testClient.getName(),
                "deposito",
                depositAmount,
                "01/09/2024",
                testCompany.getBalance() 
        );
        assertEquals(expectedEmailBody, sentEmail.getBody());
    }
}
