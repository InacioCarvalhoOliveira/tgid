package com.br.tgid.tgid.controller;

import com.br.tgid.tgid.service.ClientService;
import com.br.tgid.tgid.service.CompanyService;
import com.br.tgid.tgid.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private CompanyService companyService;

    @PostMapping("/transactions")
    public ResponseEntity<String> executeTransaction(@RequestBody TransactionRequest request) {
        try {
            boolean result = transactionService.runTransaction(
                    request.getType(),
                    request.getValue(),
                    request.getCompanyId(),
                    request.getClientId(),
                    request.getDate(),
                    request.getCallbackURL(),
                    request.getNotify());

            if (result) {
                return ResponseEntity.ok("Transação realizada com sucesso.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Falha na transação.");
            }
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor.");
        }
    }
}

class TransactionRequest {
    private String type;
    private double value;
    private Long companyId;
    private Long clientId;
    private String date;
    private String callbackURL;
    private String notify;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCallbackURL() {
        return callbackURL;
    }

    public void setCallbackURL(String callbackURL) {
        this.callbackURL = callbackURL;
    }

    public String getNotify() {
        return notify;
    }

    public void setNotify(String notify) {
        this.notify = notify;
    }
}
