package com.acme.checkout.service;

import com.acme.checkout.domain.model.CreditCard;
import com.acme.checkout.domain.model.PaymentStatus;

import java.math.BigDecimal;

public interface CreditCardService {

    /**
     * Processes the payment of the credit card and returns the status (PRE_AUTHORIZED, REFUSED)
     *
     * @param card
     * @param cvv
     * @param name
     * @param cpf
     * @param amount
     * @return
     */
    PaymentStatus processPayment(CreditCard card, Integer cvv, String name, Long cpf, BigDecimal amount);

}
