package com.acme.checkout.api.model;

import com.acme.checkout.api.controllers.PaymentController;
import com.acme.checkout.domain.model.PaymentType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.acme.checkout.domain.model.Payment;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PaymentVO {

    @ApiModelProperty(value = "paymentGuid", example = "cda4a59d-fb31-43e0-8560-c337b0815a05", position = 2)
    private String guid;

    private BuyerVO buyer;

    private PaymentDetailsVO payment;

    private List<LinkVO> _links;

    @ApiModelProperty(value = "status", example = "PENDING", required = true, position = 3)
    private String status;

    public PaymentVO apply(Payment payment) {
        PaymentVO paymentVO = new PaymentVO();

        if (payment == null) {
            return paymentVO;
        }

        paymentVO.setGuid(payment.getGuid());

        BuyerVO buyer = BuyerVO
                .builder()
                .name(payment.getName())
                .email(payment.getEmail())
                .cpf(payment.getCpf() == null ? null : Long.toString(payment.getCpf()))
                .build();
        paymentVO.setBuyer(buyer);

        PaymentDetailsVO paymentDetails = PaymentDetailsVO
                .builder()
                .amount(payment.getAmount())
                .type(payment.getType() == null ? null : payment.getType().name())
                .build();

        if (payment.getType() == PaymentType.BOLETO) {
            paymentDetails.setBoletoNumber(payment.getBoletoNumber());
        } else if (payment.getType() == PaymentType.CREDIT_CARD) {
            CreditCardVO cardVO = CreditCardVO.apply(payment.getCard());
            paymentDetails.setCard(cardVO);
        }

        List<LinkVO> links = new ArrayList<>();
        links.add(LinkVO.builder().rel("self").href(PaymentController.URL + "/" + payment.getGuid()).build());
        paymentVO.set_links(links);

        paymentVO.setPayment(paymentDetails);

        paymentVO.setStatus(payment.getStatus() == null ? null : payment.getStatus().name());

        return paymentVO;
    }


}
