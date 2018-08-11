package com.acme.checkout.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResponseMessage {

    @ApiModelProperty(value = "the error code")
    private String code;

    @ApiModelProperty(value = "the error description")
    private String description;

}
