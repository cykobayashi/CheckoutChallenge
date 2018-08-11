package com.acme.checkout.service;

import com.acme.checkout.domain.model.Payment;

public interface PaymentService {

    /**
     * Find a customer payment with the given guid.
     *
     * @param customerGuid
     * @param guid
     * @return
     */
    Payment findByGuid(String customerGuid, String guid);

    /**
     * Creates a new payment record. There are two types of payment: boleto and credit
     * card payment. The creation of a payment does not guarantee that the amount of
     * money was transferred (to check this, consult the payment status).
     *
     * @param customerGuid
     * @param payment
     * @param cvv
     * @return
     */
    Payment create(String customerGuid, Payment payment, Integer cvv);

}
