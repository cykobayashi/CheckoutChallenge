package com.acme.checkout.service.impl;

import com.acme.checkout.domain.model.CreditCard;
import com.acme.checkout.domain.model.Payment;
import com.acme.checkout.domain.model.PaymentStatus;
import com.acme.checkout.domain.repositories.PaymentRepository;
import com.acme.checkout.domain.repositories.UserRepository;
import com.acme.checkout.exceptions.NotFoundException;
import com.acme.checkout.exceptions.UnexpectedException;
import com.acme.checkout.exceptions.ValidationException;
import com.acme.checkout.service.BoletoService;
import com.acme.checkout.service.CreditCardService;
import com.acme.checkout.service.PaymentService;
import com.acme.checkout.service.messages.CommonMessages;
import com.acme.checkout.service.messages.PaymentMessages;
import com.acme.checkout.validators.CpfValidator;
import com.acme.checkout.validators.CreditCardNumberValidator;
import com.acme.checkout.validators.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repository;

    private final UserRepository userRepository;

    private final BoletoService boletoService;

    private final CreditCardService creditCardService;

    @Autowired
    public PaymentServiceImpl(PaymentRepository repository, UserRepository userRepository, BoletoService boletoService, CreditCardService creditCardService) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.boletoService = boletoService;
        this.creditCardService = creditCardService;
    }

    @Override
    public Payment findByGuid(String customerGuid, String guid) {

        validateCustomerGuid(customerGuid);

        if (StringUtils.isEmpty(guid)) {
            throw new ValidationException(CommonMessages.Validations.GUID_NULL.getCode());
        }

        Payment fromDB = repository.findByGuid(customerGuid, guid);
        if (fromDB == null) {
            throw new NotFoundException(PaymentMessages.Validations.PAYMENT_NOT_FOUND.getCode());
        }

        return fromDB;
    }

    @Override
    public Payment create(String customerGuid, Payment payment, Integer cvv) {

        validateCustomerGuid(customerGuid);

        List<String> validationsErrors = validate(payment, cvv);
        if (!validationsErrors.isEmpty()) {
            throw new ValidationException(validationsErrors);
        }

        payment.setCustomerGuid(customerGuid);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setGuid(UUID.randomUUID().toString());

        switch (payment.getType()) {
            case BOLETO:
                String boletoNumber = boletoService.generateBoletoNumber(payment.getName(), payment.getCpf(), payment.getAmount());
                payment.setBoletoNumber(boletoNumber);
                break;
            case CREDIT_CARD:
                PaymentStatus paymentStatus = creditCardService.processPayment(payment.getCard(), cvv, payment.getName(), payment.getCpf(), payment.getAmount());
                payment.setStatus(paymentStatus);
        }

        return repository.save(payment);
    }

    private List<String> validate(Payment payment, Integer cvv) {

        List<String> validations = new ArrayList<>();

        validations.addAll(validateBuyer(payment));
        validations.addAll(validatePayment(payment, cvv));

        return validations;

    }

    private Collection<? extends String> validateBuyer(Payment payment) {

        List<String> validations = new ArrayList<>();

        if (payment.getCpf() == null || !CpfValidator.isValid(Long.toString(payment.getCpf()))) {
            validations.add(PaymentMessages.Validations.BUYER_CPF_EMPTY_OR_INVALID.getCode());
        }
        if (StringUtils.isEmpty(payment.getName())) {
            validations.add(PaymentMessages.Validations.BUYER_NAME_EMPTY_OR_INVALID.getCode());
        }
        if (payment.getEmail() == null || !EmailValidator.isValid(payment.getEmail())) {
            validations.add(PaymentMessages.Validations.BUYER_EMAIL_EMPTY_OR_INVALID.getCode());
        }

        return validations;

    }

    private Collection<? extends String> validatePayment(Payment payment, Integer cvv) {

        List<String> validations = new ArrayList<>();

        if (payment.getAmount() == null) {
            validations.add(PaymentMessages.Validations.PAYMENT_IS_EMPTY.getCode());
        } else if (payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            validations.add(PaymentMessages.Validations.ZERO_OR_NEGATIVE_AMOUNT.getCode());
        }

        if (payment.getType() == null) {
            validations.add(PaymentMessages.Validations.PAYMENT_TYPE_IS_EMPTY_OR_INVALID.getCode());
        } else {
            switch (payment.getType()) {
                case BOLETO:
                    if (payment.getCard() != null) {
                        validations.add(PaymentMessages.Validations.CREDIT_CARD_MUST_BE_NULL_FOR_BOLETO.getCode());
                    }
                    break;
                case CREDIT_CARD:
                    validations.addAll(validateCreditCard(payment.getCard(), cvv));
                    break;
                default:
                    validations.add(PaymentMessages.Validations.PAYMENT_TYPE_NOT_SUPPORTED.getCode());
                    break;
            }
        }

        return validations;

    }

    private Collection<? extends String> validateCreditCard(CreditCard card, Integer cvv) {

        List<String> validations = new ArrayList<>();

        if (card == null) {
            validations.add(PaymentMessages.Validations.CREDIT_CARD_EMPTY.getCode());
            return validations;
        }

        if (card.getExpirationDate() == null) {
            validations.add(PaymentMessages.Validations.CREDIT_CARD_EXPIRATION_EMPTY.getCode());
        }
        if (StringUtils.isEmpty(card.getHolderName())) {
            validations.add(PaymentMessages.Validations.CREDIT_CARD_HOLDER_NAME_EMPTY.getCode());
        }
        if (!CreditCardNumberValidator.isValid(Long.toString(card.getNumber()))) {
            validations.add(PaymentMessages.Validations.CREDIT_CARD_NUMBER_EMPTY_OR_INVALID.getCode());
        }
        if (cvv == null) {
            validations.add(PaymentMessages.Validations.CREDIT_CARD_CVV_EMPTY.getCode());
        }

        return validations;

    }

    private void validateCustomerGuid(String customerGuid) {

        if (customerGuid == null) {
            throw new UnexpectedException(PaymentMessages.Errors.INVALID_CUSTOMER_GUID.getCode());
        }

        if (userRepository.findByGuid(customerGuid) == null) {
            throw new UnexpectedException(PaymentMessages.Errors.INVALID_CUSTOMER_GUID.getCode());
        }

    }

}
