package com.acme.checkout.service.impl;

import com.acme.checkout.domain.model.User;
import com.acme.checkout.domain.repositories.UserRepository;
import com.acme.checkout.service.UserAuthenticationService;
import com.acme.checkout.validators.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserAuthenticationServiceImpl implements UserAuthenticationService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final UserRepository userRepository;

    @Autowired
    public UserAuthenticationServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> findByToken(String email) {
        if (!EmailValidator.isValid(email)) {
            LOGGER.warn("Invalid email: " + email);
            return Optional.empty();
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            LOGGER.info("Adding new user: " + email);
            user = userRepository.save(User
                    .builder()
                    .email(email)
                    .guid(UUID.randomUUID().toString())
                    .build());
        }

        return Optional.of(user);
    }

}
