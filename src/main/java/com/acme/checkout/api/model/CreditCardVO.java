package com.acme.checkout.api.model;

import com.acme.checkout.domain.model.CreditCard;
import com.acme.checkout.domain.model.CreditCardBrand;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.format.DateTimeFormatter;

@Data
public class CreditCardVO {

    @ApiModelProperty(value = "holder name", example = "Fulano Neves", required = true, position = 1)
    private String holderName;

    @ApiModelProperty(value = "number", example = "4325024774378962", required = true, position = 2)
    private Long number;

    @ApiModelProperty(value = "expiration date (month/year)", example = "02/2031", required = true, position = 3)
    private String expirationDate;

    @ApiModelProperty(value = "brand", example = "Generic Credit Card", required = true, position = 4)
    private String brand;

    public static CreditCardVO apply(CreditCard card) {
        CreditCardVO vo = new CreditCardVO();
        vo.setHolderName(card.getHolderName());
        vo.setNumber(card.getNumber());

        DateTimeFormatter df = DateTimeFormatter.ofPattern("MM/yyyy");
        vo.setExpirationDate(df.format(card.getExpirationDate()));
        vo.setBrand(CreditCardBrand.detect(card.getNumber()).name());

        return vo;
    }


}