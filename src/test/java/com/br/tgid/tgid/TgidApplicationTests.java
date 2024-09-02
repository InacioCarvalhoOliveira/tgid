package com.br.tgid.tgid;

import com.br.tgid.tgid.service.ClientService;
import com.br.tgid.tgid.service.CompanyService;
import com.br.tgid.tgid.service.TransactionService;
import com.br.tgid.tgid.entity.Client;
import com.br.tgid.tgid.entity.Company;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    private Client testClient;
    private Company testCompany;

    @BeforeEach
    void setUp() {
        // Initialize test data
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
        // Setup
        double depositAmount = 100.00;
        double initialCompanyBalance = testCompany.getBalance();
        
        // Act
        transactionService.runTransaction("deposito", depositAmount, 8L, 1L, "01/09/2024", "https://webhook.site/97a8d9f2-17f1-4412-8dea-1b3f2c", "email");
        
        // Assert
        double updatedCompanyBalance = companyService.findById(8L).get().getBalance();
        assertEquals(initialCompanyBalance + depositAmount, updatedCompanyBalance, "O saldo da empresa não foi atualizado corretamente após o depósito.");
    }

    @Test
    @Transactional
    void testSuccessfulWithdrawal() {
        // Setup
        double withdrawalAmount = 100.00;
        double initialClientBalance = testClient.getBalance();
        double initialCompanyBalance = testCompany.getBalance();
        
        // Act
        transactionService.runTransaction("saque", withdrawalAmount, 8L, 1L, "01/09/2024", "https://webhook.site/97a8d9f2-17f1-4412-8dea-1b3f2c", "email");
        
        // Assert
        double updatedClientBalance = clientService.findById(1L).get().getBalance();
        double updatedCompanyBalance = companyService.findById(8L).get().getBalance();
        
        assertEquals(initialClientBalance - (withdrawalAmount + withdrawalAmount * 0.05), updatedClientBalance, "O saldo do cliente não foi atualizado corretamente após o saque.");
        assertEquals(initialCompanyBalance + withdrawalAmount, updatedCompanyBalance, "O saldo da empresa não foi atualizado corretamente após o saque.");
    }
}
