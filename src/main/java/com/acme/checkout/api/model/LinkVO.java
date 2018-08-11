package com.acme.checkout.api.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LinkVO {

    private String rel;
    private String href;

}