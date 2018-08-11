package com.acme.checkout.domain.model;

import org.springframework.security.core.GrantedAuthority;

public enum Privilege implements GrantedAuthority {

    ANONYMOUS;

    @Override
    public String getAuthority() {
        return this.name();
    }

}
