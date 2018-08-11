package com.acme.checkout.test.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
        "com.acme.checkout.domain",
        "com.acme.checkout.domain.model",
        "com.acme.checkout.domain.repositories",
        "com.acme.checkout.domain.converters",
        "com.acme.checkout.service"
}, lazyInit = true)
public class BusinessTestConfiguration {

}
