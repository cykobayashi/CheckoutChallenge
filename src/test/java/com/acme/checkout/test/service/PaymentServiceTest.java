package com.acme.checkout.test.service;

import com.acme.checkout.domain.model.CreditCard;
import com.acme.checkout.domain.model.Payment;
import com.acme.checkout.domain.model.PaymentType;
import com.acme.checkout.domain.model.User;
import com.acme.checkout.domain.repositories.UserRepository;
import com.acme.checkout.exceptions.UnexpectedException;
import com.acme.checkout.exceptions.ValidationException;
import com.acme.checkout.service.PaymentService;
import com.acme.checkout.service.messages.PaymentMessages;
import com.acme.checkout.test.config.BusinessLayerTestSupport;
import com.acme.checkout.test.config.BusinessTestConfiguration;
import com.acme.checkout.test.config.MongoTestConfig;
import com.mongodb.Mongo;
import org.junit.*;
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
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        MongoTestConfig.class,
        BusinessTestConfiguration.class
})
public class PaymentServiceTest extends BusinessLayerTestSupport {

	@Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private PaymentService paymentService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private Mongo mongo;

	private static final String USER_GUID = "7c84eb0c-4c92-40d5-9079-4f0cb440656a";

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
	public void create_shouldFailCreateEmptyPayment() {
		Payment payment = Payment.builder().build();

		paymentService.create(USER_GUID, payment, null);

		expectedEx.expect(ValidationException.class);
		expectedEx.expect(hasProperty("messagesCode", notNullValue()));
	}

	@Test
	public void create_shouldCreatePaymentWithBoleto() {
		Payment payment = Payment.builder().build();

		payment.setName(BUYER_NAME);
		payment.setCpf(BUYER_CPF);
		payment.setEmail(BUYER_EMAIL);

		payment.setType(PaymentType.BOLETO);
		payment.setAmount(BigDecimal.valueOf(200.0));

		Payment paymentDB = paymentService.create(USER_GUID, payment, null);

		assertNotNull(paymentDB);
		assertNotNull(paymentDB.getId());
		assertNotNull(paymentDB.getGuid());

		assertEquals(USER_GUID, paymentDB.getCustomerGuid());

		assertEquals(PaymentType.BOLETO, paymentDB.getType());
		assertNotNull(paymentDB.getBoletoNumber());

		assertEquals(BUYER_NAME, paymentDB.getName());
		assertEquals(Long.toString(BUYER_CPF), Long.toString(paymentDB.getCpf()));
		assertEquals(BUYER_EMAIL, paymentDB.getEmail());

	}

	@Test(expected = ValidationException.class)
	public void create_shouldFailWithZeroAmount() {
		Payment payment = Payment.builder().build();

		payment.setName(BUYER_NAME);
		payment.setCpf(BUYER_CPF);
		payment.setEmail(BUYER_EMAIL);

		payment.setType(PaymentType.BOLETO);
		payment.setAmount(BigDecimal.ZERO);

		paymentService.create(USER_GUID, payment, null);

		expectedEx.expect(ValidationException.class);
		expectedEx.expect(hasProperty("messagesCode", contains(PaymentMessages.Validations.ZERO_OR_NEGATIVE_AMOUNT.getCode())));

	}

	@Test(expected = ValidationException.class)
	public void create_shouldFailWithNegativeAmount() {
		Payment payment = Payment.builder().build();

		payment.setName(BUYER_NAME);
		payment.setCpf(BUYER_CPF);
		payment.setEmail(BUYER_EMAIL);

		payment.setType(PaymentType.BOLETO);
		payment.setAmount(BigDecimal.valueOf(-900.0));

		paymentService.create(USER_GUID, payment, null);

		expectedEx.expect(ValidationException.class);
		expectedEx.expect(hasProperty("messagesCode", contains(PaymentMessages.Validations.ZERO_OR_NEGATIVE_AMOUNT.getCode())));

	}

	@Test(expected = UnexpectedException.class)
	public void create_shouldFailWithoutCustomerGuid() {
		Payment payment = Payment.builder().build();

		payment.setName(BUYER_NAME);
		payment.setCpf(BUYER_CPF);
		payment.setEmail(BUYER_EMAIL);

		payment.setType(PaymentType.BOLETO);
		payment.setAmount(BigDecimal.valueOf(200.0));

		paymentService.create(null, payment, null);

		expectedEx.expect(UnexpectedException.class);
		expectedEx.expect(hasProperty("messagesCode", contains(PaymentMessages.Errors.INVALID_CUSTOMER_GUID.getCode())));

	}

	@Test
	public void create_shouldCreatePaymentWithCreditCard(){
		Payment payment = Payment.builder().build();

		payment.setName(BUYER_NAME);
		payment.setCpf(BUYER_CPF);
		payment.setEmail(BUYER_EMAIL);

		payment.setAmount(BigDecimal.valueOf(200.0));

		payment.setCard(CreditCard
				.builder()
				.number(CREDIT_CARD_NUMBER)
				.expirationDate(CREDIT_CARD_EXPIRATION)
				.holderName(CREDIT_CARD_HOLDER_NAME)
				.build());
		payment.setType(PaymentType.CREDIT_CARD);

		Payment paymentDB = paymentService.create(USER_GUID, payment, CREDIT_CARD_CVV);

		assertNotNull(paymentDB);
		assertNotNull(paymentDB.getId());
		assertNotNull(paymentDB.getGuid());

        assertEquals(USER_GUID, paymentDB.getCustomerGuid());

		assertEquals(PaymentType.CREDIT_CARD, paymentDB.getType());
		assertNotNull(paymentDB.getBoletoNumber(), isEmptyOrNullString());

		assertEquals(BUYER_NAME, paymentDB.getName());
		assertEquals(Long.toString(BUYER_CPF), Long.toString(paymentDB.getCpf()));
		assertEquals(BUYER_EMAIL, paymentDB.getEmail());

	}

	@Test(expected = ValidationException.class)
	public void create_shouldFailWithEmptyCVV(){
		Payment payment = Payment.builder().build();

		payment.setName(BUYER_NAME);
		payment.setCpf(BUYER_CPF);
		payment.setEmail(BUYER_EMAIL);

		payment.setAmount(BigDecimal.valueOf(200.0));

		payment.setCard(CreditCard
				.builder()
				.number(CREDIT_CARD_NUMBER)
				.expirationDate(CREDIT_CARD_EXPIRATION)
				.holderName(CREDIT_CARD_HOLDER_NAME)
				.build());
		payment.setType(PaymentType.CREDIT_CARD);

		Payment paymentDB = paymentService.create(USER_GUID, payment, null);

		expectedEx.expect(UnexpectedException.class);
		expectedEx.expect(hasProperty("messagesCode", contains(PaymentMessages.Validations.CREDIT_CARD_CVV_EMPTY.getCode())));

	}

	@Test(expected = ValidationException.class)
	public void create_shouldFailWithEmptyCreditCard(){
		Payment payment = Payment.builder().build();

		payment.setName(BUYER_NAME);
		payment.setCpf(BUYER_CPF);
		payment.setEmail(BUYER_EMAIL);

		payment.setType(PaymentType.CREDIT_CARD);
		payment.setAmount(BigDecimal.valueOf(200.0));

		paymentService.create(USER_GUID, payment, CREDIT_CARD_CVV);

		expectedEx.expect(UnexpectedException.class);
		expectedEx.expect(hasProperty("messagesCode", contains(PaymentMessages.Validations.CREDIT_CARD_EMPTY.getCode())));

	}

}
