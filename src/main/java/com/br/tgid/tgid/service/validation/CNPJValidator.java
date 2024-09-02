package com.br.tgid.tgid.service.validation;

public class CNPJValidator {

    public static boolean isvalidCnpj(String cnpj) {
        if (cnpj == null || cnpj.trim().isEmpty()) {
            return false;
        }

        // Remove non-numeric characters
        cnpj = cnpj.replaceAll("[./-]", "").trim();

        if (cnpj.length() != 14) {
            return false;
        }

        // Check if all digits are the same (e.g., "11111111111111")
        if (cnpj.matches("(\\d)\\1{13}")) {
            return false;
        }

        String cnpjBase = cnpj.substring(0, 12);
        String calculatedCnpj = cnpjBase + calculateVerificationDigit(cnpjBase);

        return cnpj.equals(calculatedCnpj);
    }

    private static String calculateVerificationDigit(String cnpjBase) {
        int firstDigit = calculateDigit(cnpjBase, new int[] { 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2 });
        int secondDigit = calculateDigit(cnpjBase + firstDigit, new int[] { 6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2 });

        return String.valueOf(firstDigit) + String.valueOf(secondDigit);
    }

    private static int calculateDigit(String cnpj, int[] weights) {
        int sum = 0;

        for (int i = 0; i < cnpj.length(); i++) {
            int digit = cnpj.charAt(i) - '0';
            sum += digit * weights[i];
        }

        int remainder = sum % 11;
        return (remainder < 2) ? 0 : 11 - remainder;
    }
}
