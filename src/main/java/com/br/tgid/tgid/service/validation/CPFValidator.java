package com.br.tgid.tgid.service.validation;

import java.util.stream.IntStream;

public class CPFValidator {

    public static boolean isValidCPF(Object value) {
        String cpf = (String) value;

        if (cpf == null || cpf.trim().isEmpty()) {
            return true;
        }

        cpf = cpf.trim().replace(".", "").replace("-", "");

        if (cpf.length() != 11) {
            return false;
        }

        // Check if all digits are the same
        for (int j = 0; j < 10; j++) {
            if (cpf.equals(String.valueOf(j).repeat(11))) {
                return false;
            }
        }

        // Calculate the first verification digit
        final String tempCpf = cpf.substring(0, 9);
        int sum = IntStream.range(0, 9)
                .map(i -> (tempCpf.charAt(i) - '0') * (10 - i))
                .sum();

        int remainder = sum % 11;
        remainder = remainder < 2 ? 0 : 11 - remainder;

        String digit = String.valueOf(remainder);
        String cpfWithFirstDigit = tempCpf + digit; // Create a new variable

        // Calculate the second verification digit
        sum = IntStream.range(0, 10)
                .map(i -> (cpfWithFirstDigit.charAt(i) - '0') * (11 - i))
                .sum();

        remainder = sum % 11;
        remainder = remainder < 2 ? 0 : 11 - remainder;

        digit += String.valueOf(remainder);

        // Check if the calculated digits match the CPF digits
        return cpf.endsWith(digit);
    }
}
