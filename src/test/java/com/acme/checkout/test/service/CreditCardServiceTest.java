package com.acme.checkout.test.service;

import com.acme.checkout.domain.model.*;
import com.acme.checkout.domain.repositories.UserRepository;
import com.acme.checkout.exceptions.ValidationException;
import com.acme.checkout.service.CreditCardService;
import com.acme.checkout.service.messages.PaymentMessages;
import com.acme.checkout.test.config.BusinessLayerTestSupport;
import com.acme.checkout.test.config.BusinessTestConfiguration;
import com.acme.checkout.test.config.MongoTestConfig;
import com.mongodb.Mongo;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.acme.checkout.test.config.MongoTestConfig.DATABASE_TEST_NAME;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        MongoTestConfig.class,
        BusinessTestConfiguration.class
})
public class CreditCardServiceTest extends BusinessLayerTestSupport {

	@Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private CreditCardService creditCardService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private Mongo mongo;

	private static final String USER_GUID = "7c84eb0c-4c92-40d5-9079-4f0cb440656a";
	private static final String PAYMENTGUID = "157f393c-5fc7-4bfc-9b61-5b6be1fa79ca";
	private static final String NONEXIST_PAYMENTGUID = "157f393c-5fc7-4bfc-9b61-5b6be1fa88ag";
	//private final

	private static final String BUYER_NAME = "RANDOM NAME";
	private static final long BUYER_CPF = 27338975868L;
	private static final String BUYER_EMAIL = "random@email.com";

	private static final long CREDIT_CARD_NUMBER = 4325024774378962L;
	private static final String CREDIT_CARD_HOLDER_NAME = "Fulano Neves";
	private static final LocalDate CREDIT_CARD_EXPIRATION = LocalDate.of(2058, 1, 31);
	private static final int CREDIT_CARD_CVV = 711;

	@Before
	public void setUp() {
		userRepository.save(User.builder().guid(USER_GUID).build());
	}

	@After
	public void down() {
		mongo.dropDatabase(DATABASE_TEST_NAME);
	}

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Test(expected = ValidationException.class)
	public void processPayment_shouldFailWithZeroAmount() {
		CreditCard card = CreditCard
				.builder()
				.holderName(CREDIT_CARD_HOLDER_NAME)
				.number(CREDIT_CARD_NUMBER)
				.expirationDate(CREDIT_CARD_EXPIRATION)
				.build();

		creditCardService.processPayment(card, CREDIT_CARD_CVV, BUYER_NAME, BUYER_CPF, BigDecimal.ZERO);

		expectedEx.expect(ValidationException.class);
		expectedEx.expect(hasProperty("messagesCode", notNullValue()));
		expectedEx.expect(hasProperty("messagesCode", is(PaymentMessages.Validations.ZERO_OR_NEGATIVE_AMOUNT.getCode())));
	}

    @Test(expected = ValidationException.class)
    public void processPayment_shouldFailWithNullAmount() {
        CreditCard card = CreditCard
                .builder()
                .holderName(CREDIT_CARD_HOLDER_NAME)
                .number(CREDIT_CARD_NUMBER)
                .expirationDate(CREDIT_CARD_EXPIRATION)
                .build();

        creditCardService.processPayment(card, CREDIT_CARD_CVV, BUYER_NAME, BUYER_CPF, null);

        expectedEx.expect(ValidationException.class);
        expectedEx.expect(hasProperty("messagesCode", notNullValue()));
        expectedEx.expect(hasProperty("messagesCode", is(PaymentMessages.Validations.AMOUNT_IS_EMPTY.getCode())));
    }

    @Test(expected = ValidationException.class)
    public void processPayment_shouldFailWithUnkownBrand() {
        CreditCard card = CreditCard
                .builder()
                .holderName(CREDIT_CARD_HOLDER_NAME)
                .number(0L)
                .expirationDate(CREDIT_CARD_EXPIRATION)
                .build();

        creditCardService.processPayment(card, CREDIT_CARD_CVV, BUYER_NAME, BUYER_CPF, BigDecimal.valueOf(20.0));

        expectedEx.expect(ValidationException.class);
        expectedEx.expect(hasProperty("messagesCode", notNullValue()));
        expectedEx.expect(hasProperty("messagesCode", is(PaymentMessages.Validations.CREDIT_CARD_BRAND_NOT_SUPPORTED.getCode())));
    }


    @Test(expected = ValidationException.class)
    public void processPayment_shouldFailWithNullExpiration() {
        CreditCard card = CreditCard
                .builder()
                .holderName(CREDIT_CARD_HOLDER_NAME)
                .number(CREDIT_CARD_NUMBER)
                .expirationDate(null)
                .build();

        creditCardService.processPayment(card, CREDIT_CARD_CVV, BUYER_NAME, BUYER_CPF, BigDecimal.valueOf(20.0));

        expectedEx.expect(ValidationException.class);
        expectedEx.expect(hasProperty("messagesCode", notNullValue()));
        expectedEx.expect(hasProperty("messagesCode", is(PaymentMessages.Validations.CREDIT_CARD_EXPIRATION_EMPTY.getCode())));
    }

    @Test
	public void processPayment_shouldPreAuthorized() {
		CreditCard card = CreditCard
				.builder()
				.holderName(CREDIT_CARD_HOLDER_NAME)
				.number(CREDIT_CARD_NUMBER)
				.expirationDate(CREDIT_CARD_EXPIRATION)
				.build();

		PaymentStatus status =
			creditCardService.processPayment(card, CREDIT_CARD_CVV, BUYER_NAME, BUYER_CPF, BigDecimal.valueOf(10.0));

		assertEquals(status, PaymentStatus.PRE_AUTHORIZED);

	}

    @Test
    public void processPayment_shouldRefuseBigAmount() {
        CreditCard card = CreditCard
                .builder()
                .holderName(CREDIT_CARD_HOLDER_NAME)
                .number(CREDIT_CARD_NUMBER)
                .expirationDate(CREDIT_CARD_EXPIRATION)
                .build();

        PaymentStatus status =
                creditCardService.processPayment(card, CREDIT_CARD_CVV, BUYER_NAME, BUYER_CPF, BigDecimal.valueOf(10000000.0));

        assertEquals(status, PaymentStatus.REFUSED);

    }

    @Test
    public void processPayment_shouldFailWithExpiredCard() {
        CreditCard card = CreditCard
                .builder()
                .holderName(CREDIT_CARD_HOLDER_NAME)
                .number(CREDIT_CARD_NUMBER)
                .expirationDate(LocalDate.of(1900, 1, 1))
                .build();

        PaymentStatus status =
                creditCardService.processPayment(card, CREDIT_CARD_CVV, BUYER_NAME, BUYER_CPF, BigDecimal.valueOf(10.0));

        assertEquals(status, PaymentStatus.EXPIRED);

    }

}
