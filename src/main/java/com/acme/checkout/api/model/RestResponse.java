package com.acme.checkout.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(Include.NON_EMPTY)
public class RestResponse<T> {

    public enum Status { SUCCESS, ERROR }

    @ApiModelProperty(value = "the timestamp from response")
    private Long timestamp;
    @ApiModelProperty(value = "the http status code from response")
    private int code;
    @ApiModelProperty(value = "the status from response", allowableValues = "SUCCESS, ERROR")
    private String status;
    @ApiModelProperty(value = "the error messages")
    private List<ResponseMessage> messages = new ArrayList<>();
    @JsonInclude()
    @ApiModelProperty(value = "the response")
    private T result;

    public void setStatus(Status status) {
        this.status = status.name().toLowerCase();
    }

}
