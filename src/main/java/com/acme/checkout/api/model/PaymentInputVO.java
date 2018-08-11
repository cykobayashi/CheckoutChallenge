package com.acme.checkout.api.model;

import com.acme.checkout.domain.model.Payment;
import com.acme.checkout.domain.model.PaymentType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PaymentInputVO {

    private BuyerVO buyer;

    private PaymentDetailsInputVO payment;

    public PaymentInputVO apply(Payment payment) {
        PaymentInputVO paymentVO = new PaymentInputVO();

        if (payment == null) {
            return paymentVO;
        }

        BuyerVO buyer = BuyerVO
                .builder()
                .name(payment.getName())
                .email(payment.getEmail())
                .cpf(payment.getCpf() == null ? null : Long.toString(payment.getCpf()))
                .build();
        paymentVO.setBuyer(buyer);

        PaymentDetailsInputVO paymentDetails = PaymentDetailsInputVO
                .builder()
                .amount(payment.getAmount())
                .type(payment.getType() == null ? null : payment.getType().name())
                .build();

        if (payment.getType() == PaymentType.CREDIT_CARD) {
            CreditCardInputVO cardVO = CreditCardInputVO.apply(payment.getCard());
            paymentDetails.setCard(cardVO);
        }

        paymentVO.setPayment(paymentDetails);

        return paymentVO;
    }

}
