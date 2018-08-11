package com.acme.checkout.validators;

public class CpfValidator {

    private static final int[] weightCPF = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};

    public static boolean isValid(String cpf) {
        if (cpf == null) {
            return false;
        }
        cpf = cpf.trim().replace(".", "").replace("-", "");
        if (cpf.length() != 11) return false;

        for (int j = 0; j < 10; j++)
            if (padLeft(Integer.toString(j), Character.forDigit(j, 10)).equals(cpf))
                return false;

        Integer digit1 = calcularDigito(cpf.substring(0, 9), weightCPF);
        Integer digit2 = calcularDigito(cpf.substring(0, 9) + digit1, weightCPF);
        return cpf.equals(cpf.substring(0, 9) + digit1.toString() + digit2.toString());
    }

    private static int calcularDigito(String str, int[] weight) {
        int sum = 0;
        for (int idx = str.length() - 1, digit; idx >= 0; idx--) {
            digit = Integer.parseInt(str.substring(idx, idx + 1));
            sum += digit * weight[weight.length - str.length() + idx];
        }
        sum = 11 - sum % 11;
        return sum > 9 ? 0 : sum;
    }

    private static String padLeft(String text, char character) {
        return String.format("%11s", text).replace(' ', character);
    }


}