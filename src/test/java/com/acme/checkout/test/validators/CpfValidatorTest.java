package com.acme.checkout.test.validators;

import com.acme.checkout.validators.CpfValidator;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CpfValidatorTest {

	@Test
	public void shouldCheckValidCPFs() {
		assertTrue(CpfValidator.isValid("764.322.352-19"));
		assertTrue(CpfValidator.isValid("76432235219"));

        assertTrue(CpfValidator.isValid("726.766.947-66"));
        assertTrue(CpfValidator.isValid("842.038.381-34"));
        assertTrue(CpfValidator.isValid("886.989.937-37"));
        assertTrue(CpfValidator.isValid("770.237.356-31"));
        assertTrue(CpfValidator.isValid("494.034.748-66"));
    }

    @Test
    public void shouldCheckInvalidCPFs() {
        assertFalse(CpfValidator.isValid(null));
        assertFalse(CpfValidator.isValid(""));
        assertFalse(CpfValidator.isValid("AAA"));

        assertFalse(CpfValidator.isValid("726.766.947-60"));
        assertFalse(CpfValidator.isValid("842.038.381-30"));
        assertFalse(CpfValidator.isValid("886.989.937-30"));
        assertFalse(CpfValidator.isValid("770.237.356-30"));
        assertFalse(CpfValidator.isValid("494.034.748-60"));
    }

}
