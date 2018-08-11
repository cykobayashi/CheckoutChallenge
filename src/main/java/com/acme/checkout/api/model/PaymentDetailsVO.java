package com.acme.checkout.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PaymentDetailsVO  {

    @ApiModelProperty(value = "amount", example = "5030.25", required = true, position = 1)
    private BigDecimal amount;

    @ApiModelProperty(value = "type", example = "CREDIT_CARD", allowableValues = "BOLETO,CREDIT_CARD", required = true, position = 2)
    private String type;

    private CreditCardVO card;

    @ApiModelProperty(value = "boletoNumber", example = "000907794973046975892919507059799990000503025", position = 3)
    private String boletoNumber;

}
