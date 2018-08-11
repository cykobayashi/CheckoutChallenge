package com.acme.checkout.test.validators;

import com.acme.checkout.validators.EmailValidator;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EmailValidatorTest {

    @Test
    public void shouldCheckValidEmails() {

        assertTrue(EmailValidator.isValid("email@example.com"));
        assertTrue(EmailValidator.isValid("firstname.lastname@example.com"));
        assertTrue(EmailValidator.isValid("email@subdomain.example.com"));
        assertTrue(EmailValidator.isValid("firstname+lastname@example.com"));
        assertTrue(EmailValidator.isValid("1234567890@example.com"));
        assertTrue(EmailValidator.isValid("email@example-one.com"));
        assertTrue(EmailValidator.isValid("_______@example.com"));
        assertTrue(EmailValidator.isValid("email@example.name"));
        assertTrue(EmailValidator.isValid("email@example.museum"));
        assertTrue(EmailValidator.isValid("email@example.co.jp"));
        assertTrue(EmailValidator.isValid("firstname-lastname@example.com"));

    }

    @Test
    public void shouldCheckInvalidEmails() {

        assertFalse(EmailValidator.isValid(null));
        assertFalse(EmailValidator.isValid(""));

        assertFalse(EmailValidator.isValid("plainaddress"));

        assertFalse(EmailValidator.isValid("#@%^%#$@#$@#.com"));
        assertFalse(EmailValidator.isValid("@example.com"));
        assertFalse(EmailValidator.isValid("Joe Smith <email@example.com>"));
        assertFalse(EmailValidator.isValid("email.example.com"));
        assertFalse(EmailValidator.isValid("email@example@example.com"));
        assertFalse(EmailValidator.isValid(".email@example.com"));
        assertFalse(EmailValidator.isValid("email.@example.com"));
        assertFalse(EmailValidator.isValid("email..email@example.com"));
        assertFalse(EmailValidator.isValid("email@example.com (Joe Smith)"));
        assertFalse(EmailValidator.isValid("email@example"));
        assertFalse(EmailValidator.isValid("email@111.222.333.44444"));
        assertFalse(EmailValidator.isValid("email@example..com"));
        assertFalse(EmailValidator.isValid("Abc..123@example.com"));

    }

}
