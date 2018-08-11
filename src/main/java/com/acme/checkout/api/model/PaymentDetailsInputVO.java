package com.acme.checkout.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PaymentDetailsInputVO {

    @ApiModelProperty(value = "amount", example = "5030.25", required = true, position = 1)
    private BigDecimal amount;

    @ApiModelProperty(value = "type", example = "CREDIT_CARD", allowableValues = "BOLETO,CREDIT_CARD", required = true, position = 2)
    private String type;

    @ApiModelProperty(value = "paymentGuid", example = "cda4a59d-fb31-43e0-8560-c337b0815a05", position = 3)
    private CreditCardInputVO card;

}
