package com.acme.checkout.test.validators;

import com.acme.checkout.domain.model.CreditCardBrand;
import com.acme.checkout.validators.CreditCardNumberValidator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CreditCardValidatorTest {

	@Test
	public void shouldCheckValidNumbers() {

	    // VISA
		assertTrue(CreditCardNumberValidator.isValid("4936386696393820"));
		assertTrue(CreditCardNumberValidator.isValid("4024007137282793"));

		// MasterCard
        assertTrue(CreditCardNumberValidator.isValid("5442805125932605"));
        assertTrue(CreditCardNumberValidator.isValid("2720991218553688"));

        // American Express (AMEX)
        assertTrue(CreditCardNumberValidator.isValid("349726605825892"));
        assertTrue(CreditCardNumberValidator.isValid("371487695105080"));

    }

    @Test
    public void shouldCheckInvalidNumbers() {
        assertFalse(CreditCardNumberValidator.isValid(null));
        assertFalse(CreditCardNumberValidator.isValid(""));
        assertFalse(CreditCardNumberValidator.isValid("AAA"));
        assertFalse(CreditCardNumberValidator.isValid("0"));

        // VISA
        assertFalse(CreditCardNumberValidator.isValid("4936386696393821"));
        assertFalse(CreditCardNumberValidator.isValid("4024007137282791"));

        // MasterCard
        assertFalse(CreditCardNumberValidator.isValid("5442805125932601"));
        assertFalse(CreditCardNumberValidator.isValid("2720991218553681"));

        // American Express (AMEX)
        assertFalse(CreditCardNumberValidator.isValid("349726605825891"));
        assertFalse(CreditCardNumberValidator.isValid("371487695105081"));

    }

    @Test
    public void shouldDetectCreditCardBrand() {

        // VISA
        assertEquals(CreditCardBrand.VISA, CreditCardBrand.detect(4936386696393820L));
        assertEquals(CreditCardBrand.VISA, CreditCardBrand.detect(4024007137282793L));

        // MasterCard
        assertEquals(CreditCardBrand.MASTERCARD, CreditCardBrand.detect(5442805125932605L));
        assertEquals(CreditCardBrand.MASTERCARD, CreditCardBrand.detect(2720991218553688L));

        // American Express (AMEX)
        assertEquals(CreditCardBrand.AMERICAN_EXPRESS, CreditCardBrand.detect(349726605825892L));
        assertEquals(CreditCardBrand.AMERICAN_EXPRESS, CreditCardBrand.detect(371487695105080L));

    }

}
