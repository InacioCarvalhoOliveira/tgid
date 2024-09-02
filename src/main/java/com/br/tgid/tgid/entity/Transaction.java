package com.br.tgid.tgid.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Transaction {

    private static final Logger logger = LoggerFactory.getLogger(Transaction.class);

    private String type;
    private double value;
    private Company company;
    private Client client;
    private String date;
    private String callbackURL;
    private String notify;

    public Transaction(String type, double value, Company company, Client client, String date, String callbackURL,
            String notify) {
        this.type = type;
        this.value = value;
        this.company = company;
        this.client = client;
        this.date = date;
        this.callbackURL = callbackURL;
        this.notify = notify;
    }

    public boolean run() {
        boolean result = false;
        try {
            if (type.equals("deposito")) {
                logger.info("Realizando depósito...");
                client.deposit(value); // Adiciona o valor depositado ao saldo do cliente
                company.balanceUpdate(value); // Adiciona o valor total do depósito ao saldo da empresa
                result = true;
            } else if (type.equals("saque")) {
                logger.info("Tentando realizar saque...");
                double fee = value * company.getSystemTax(); // Calcula a taxa de administração
                double totalAmount = value + fee; // Valor total que o cliente precisa ter disponível

                if (client.getBalance() >= totalAmount) { // Verifica se o cliente tem saldo suficiente
                    client.withdraw(totalAmount); // Reduz o saldo do cliente pelo total (saque + taxa)
                    company.balanceUpdate(value); // Adiciona apenas o valor sacado ao saldo da empresa
                    result = true;
                }
            }

            if (result) {
                callbackSender();
                clientNotifier();
            }
        } catch (Exception e) {
            logger.error("Erro ao executar a transação: " + e.getMessage(), e);
        }
        return result;
    }

    private void callbackSender() {
        try {
            URL url = new URL(callbackURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            String jsonInputString = String.format(
                    "{\"tipo\":\"%s\", \"valor\": %.2f, \"cliente\":\"%s\", \"data\":\"%s\"}",
                    type, value, client.getName(), date);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                logger.info("Callback enviado com sucesso.");
            } else {
                logger.warn("Falha ao enviar o callback. Código de resposta: " + responseCode);
            }
        } catch (Exception e) {
            logger.error("Erro ao enviar o callback: " + e.getMessage(), e);
        }
    }

    private void clientNotifier() {
        switch (notify) {
            case "email":
                clientEmailSender();
                break;
            case "sms":
                // TODO:Implementar notificação via SMS
                break;
            default:
                logger.warn("Tipo de notificação não suportado.");
        }
    }

    private void clientEmailSender() {
        String email = client.getEmail();
        String message = String.format(
                "Olá %s, sua transação do tipo %s no valor de R$ %.2f foi realizada com sucesso em %s.",
                client.getName(), type, value, date);

        // Simulação de envio de email
        logger.info("Enviando email para " + email);
        logger.info("Mensagem: " + message);
    }
}
