package com.acme.checkout.service;

import java.math.BigDecimal;

public interface BoletoService {

    /**
     * Generates the boleto number
     *
     * @param name
     * @param cpf
     * @param amount
     * @return
     */
    String generateBoletoNumber(String name, Long cpf, BigDecimal amount);

}
