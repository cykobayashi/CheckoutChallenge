package com.acme.checkout.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BuyerVO {

    @ApiModelProperty(value = "name", example = "Fulano Neves", required = true, position = 1)
    private String name;

    @ApiModelProperty(value = "email", example = "fulano.neves@company.com", required = true, position = 2)
    private String email;

    @ApiModelProperty(value = "cpf", example = "273.389.758-68", required = true, position = 3)
    private String cpf;

}
