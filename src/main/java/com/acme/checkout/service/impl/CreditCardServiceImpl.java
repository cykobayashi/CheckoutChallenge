package com.acme.checkout.service.impl;

import com.acme.checkout.domain.model.CreditCard;
import com.acme.checkout.domain.model.CreditCardBrand;
import com.acme.checkout.domain.model.PaymentStatus;
import com.acme.checkout.exceptions.ValidationException;
import com.acme.checkout.service.CreditCardService;
import com.acme.checkout.service.messages.PaymentMessages;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class CreditCardServiceImpl implements CreditCardService {

    @Override
    public PaymentStatus processPayment(CreditCard card, Integer cvv, String name, Long cpf, BigDecimal amount) {

        if (amount == null) {
            throw new ValidationException(PaymentMessages.Validations.AMOUNT_IS_EMPTY.getCode());
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException(PaymentMessages.Validations.ZERO_OR_NEGATIVE_AMOUNT.getCode());
        }

        if (CreditCardBrand.detect(card.getNumber()) == CreditCardBrand.UNKNOWN){
            throw new ValidationException(PaymentMessages.Validations.CREDIT_CARD_BRAND_NOT_SUPPORTED.getCode());
        }

        if (card.getExpirationDate() == null) {
            throw new ValidationException(PaymentMessages.Validations.CREDIT_CARD_EXPIRATION_EMPTY.getCode());
        }
        if (card.getExpirationDate().isBefore(LocalDate.now())) {
            // expired
            return PaymentStatus.EXPIRED;
        }

        if (amount.compareTo(BigDecimal.valueOf(5000.0)) > 0) {
            // refuse
            return PaymentStatus.REFUSED;
        }

        // accept
        return PaymentStatus.PRE_AUTHORIZED;

    }

}