package com.acme.checkout.domain.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "payments")
@Data
@Builder
public class Payment {

    @Id
    private String id;
    private String guid;

    private String customerGuid;

    // Buyer
    private String name;
    private String email;
    private Long cpf;

    private BigDecimal amount;
    private PaymentType type;
    private CreditCard card;
    private PaymentStatus status;

    private String boletoNumber;

}
