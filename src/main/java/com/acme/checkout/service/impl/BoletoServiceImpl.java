package com.acme.checkout.service.impl;

import com.acme.checkout.exceptions.ValidationException;
import com.acme.checkout.service.BoletoService;
import com.acme.checkout.service.messages.PaymentMessages;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

@Service
public class BoletoServiceImpl implements BoletoService {

    @Override
    public String generateBoletoNumber(String name, Long cpf, BigDecimal amount) {

        if (amount == null) {
            throw new ValidationException(PaymentMessages.Validations.AMOUNT_IS_EMPTY.getCode());
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException(PaymentMessages.Validations.ZERO_OR_NEGATIVE_AMOUNT.getCode());
        }

        Random rnd = new Random(System.nanoTime());
        StringBuilder sb = new StringBuilder();

        // bank / currency
        sb.append("00090");

        // free field
        for (int i = 0; i < 25; i++) {
            sb.append(rnd.nextInt(10));
        }

        // check digit
        sb.append(rnd.nextInt(10));

        // expiry
        sb.append("9999");

        // amount
        sb.append(String.format("%10s", String.format("%.2f", amount).replaceAll("[^\\d]", "")).replaceAll(" ", "0"));

        return sb.toString();

    }

}
