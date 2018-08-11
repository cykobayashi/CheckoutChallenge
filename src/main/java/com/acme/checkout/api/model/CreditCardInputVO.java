package com.acme.checkout.api.model;

import com.acme.checkout.domain.model.CreditCard;
import com.acme.checkout.domain.model.CreditCardBrand;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.format.DateTimeFormatter;

@Data
public class CreditCardInputVO extends CreditCardVO {

    @ApiModelProperty(value = "card verification value", example = "711", required = true, position = 4)
    private Integer cvv;

    public static CreditCardInputVO apply(CreditCard card) {
        CreditCardInputVO vo = new CreditCardInputVO();
        vo.setHolderName(card.getHolderName());
        vo.setNumber(card.getNumber());

        DateTimeFormatter df = DateTimeFormatter.ofPattern("MM/yyyy");
        vo.setExpirationDate(df.format(card.getExpirationDate()));
        vo.setBrand(CreditCardBrand.detect(card.getNumber()).name());

        return vo;
    }

}
