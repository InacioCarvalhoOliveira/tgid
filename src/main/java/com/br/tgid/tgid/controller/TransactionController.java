package com.br.tgid.tgid.controller;

import com.br.tgid.tgid.service.ClientService;
import com.br.tgid.tgid.service.CompanyService;
import com.br.tgid.tgid.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Execute a transaction", description = "Processes a transaction based on the provided details. For testing purposes, you can use the webhook site at https://webhook.site/97a8d9f2-17f1-4412-8dea-1b3a0a2a3f2c to view the request payload.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction successfully processed", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400", description = "Invalid input parameters provided", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "404", description = "Company or Client not found", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "500", description = "Internal server error while processing transaction", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
    @PostMapping("/transactions")
    public ResponseEntity<String> executeTransaction(
            @RequestBody(description = "Details of the transaction to be executed") TransactionRequest request) {

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
    @Schema(description = "Type of the transaction", example = "saque ou deposito")
    private String type;

    @Schema(description = "Value of the transaction", example = "150.00")
    private double value;

    @Schema(description = "ID of the company", example = "1")
    private Long companyId;

    @Schema(description = "ID of the client", example = "1")
    private Long clientId;

    @Schema(description = "Date of the transaction", example = "2024/09/02")
    private String date;

    @Schema(description = "Callback URL for the transaction", example = "https://webhook.site/97a8d9f2-17f1-4412-8dea-1b3a0a2a3f2c")
    private String callbackURL;

    @Schema(description = "Notification type", example = "only email worked")
    private String notify;

    // Getters and Setters
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
