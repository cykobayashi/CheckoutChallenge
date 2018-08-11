package com.acme.checkout.service;

import com.acme.checkout.domain.model.User;

import java.util.Optional;

public interface UserAuthenticationService {

    /**
     * Finds a user by its dao-key.
     *
     * @param token user dao key
     * @return
     */
    Optional<User> findByToken(String token);

}