package com.acme.checkout.test.api.controllers;

import com.acme.checkout.api.controllers.PaymentController;
import com.acme.checkout.api.model.PaymentInputVO;
import com.acme.checkout.api.model.PaymentVO;
import com.acme.checkout.api.CrudResponseAdvice;
import com.acme.checkout.config.MessagesConfig;
import com.acme.checkout.domain.model.*;
import com.acme.checkout.domain.repositories.PaymentRepository;
import com.acme.checkout.domain.repositories.UserRepository;
import com.acme.checkout.service.messages.PaymentMessages;
import com.acme.checkout.test.api.config.WebTestConfiguration;
import com.acme.checkout.test.config.BusinessTestConfiguration;
import com.acme.checkout.test.config.MongoTestConfig;
import com.mongodb.Mongo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDate;

import static com.acme.checkout.test.config.MongoTestConfig.DATABASE_TEST_NAME;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = { PaymentController.class })
@ContextConfiguration(classes = {
        WebTestConfiguration.class,
        MongoTestConfig.class,
        BusinessTestConfiguration.class,
        MessagesConfig.class,
        CrudResponseAdvice.class
})
public class PaymentControllerTest extends WebLayerTestContext {

    private final String BASEPATH = "payments";

    private static final String GUID = "4af017c9-927a-45d1-90d4-d446d2d270ed";
    private static final String NON_EXISTING_GUID = "238b13b7-6c9e-40d9-9429-ceb8bb837353";

    private static final String BUYER_NAME = "RANDOM NAME";
    private static final long BUYER_CPF = 27338975868L;
    private static final String BUYER_EMAIL = "random@email.com";

    private static final long CREDIT_CARD_NUMBER = 4325024774378962L;
    private static final String CREDIT_CARD_HOLDER_NAME = "Fulano Neves";
    private static final LocalDate CREDIT_CARD_EXPIRATION = LocalDate.of(2058, 1, 31);
    private static final int CREDIT_CARD_CVV = 711;

    private Payment payment;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private Mongo mongo;

    @Before
    public void setUp() {

        payment = Payment
                .builder()
                .customerGuid(USER_GUID)
                .guid(GUID)
                .name(BUYER_NAME)
                .cpf(BUYER_CPF)
                .email(BUYER_EMAIL)
                .type(PaymentType.BOLETO)
                .amount(BigDecimal.valueOf(50.10))
                .build();

        paymentRepository.save(payment);

        userRepository.save(User.builder().guid(USER_GUID).build());
    }

    @After
    public void down() {
        mongo.dropDatabase(DATABASE_TEST_NAME);
    }

    @Test
    public void shouldCreatePaymentWithBoleto() throws Exception {

        getMockMvc().perform(MockMvcRequestBuilders.post(MessageFormat.format("/api/v1/{0}", BASEPATH))
                .content(getJson(new PaymentVO().apply(payment)))
                .contentType("application/json")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andDo(print())
                .andExpect(jsonPath("$.code", is(HttpStatus.CREATED.value())))
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.timestamp", greaterThan(1400000000)))
                .andExpect(jsonPath("$.result").isMap())
                .andExpect(jsonPath("$.result.guid", notNullValue()))

                .andExpect(jsonPath("$.result.buyer.name", is(BUYER_NAME)))
                .andExpect(jsonPath("$.result.buyer.email", is(BUYER_EMAIL)))
                .andExpect(jsonPath("$.result.buyer.cpf", is(Long.toString(BUYER_CPF))))

                .andExpect(jsonPath("$.result.payment.type", is(PaymentType.BOLETO.name())))
                .andExpect(jsonPath("$.result.payment.amount", is(payment.getAmount().doubleValue())))
                .andExpect(jsonPath("$.result.payment.boletoNumber", notNullValue()))
                .andExpect(jsonPath("$.result.payment", not(hasProperty("card"))))
                .andExpect(jsonPath("$.result.status", is(PaymentStatus.PENDING.name())))
        ;

    }

    @Test
    public void shouldFailWithEmptyBuyerDetails() throws Exception {

        PaymentVO paymentVO = new PaymentVO().apply(payment);
        paymentVO.setBuyer(null);

        assertBadRequest(getMockMvc().perform(MockMvcRequestBuilders.post(MessageFormat.format("/api/v1/{0}", BASEPATH))
                .content(getJson(paymentVO))
                .contentType("application/json")
                .accept(MediaType.APPLICATION_JSON)), PaymentMessages.Validations.BUYER_IS_EMPTY.getCode());

    }

    @Test
    public void shouldFailWithEmptyBuyerCPF() throws Exception {

        PaymentVO paymentVO = new PaymentVO().apply(payment);
        paymentVO.getBuyer().setCpf(null);

        assertBadRequest(getMockMvc().perform(MockMvcRequestBuilders.post(MessageFormat.format("/api/v1/{0}", BASEPATH))
                .content(getJson(paymentVO))
                .contentType("application/json")
                .accept(MediaType.APPLICATION_JSON)), PaymentMessages.Validations.BUYER_CPF_EMPTY_OR_INVALID.getCode());

    }

    @Test
    public void shouldFailWithInvalidBuyerCPF() throws Exception {

        PaymentVO paymentVO = new PaymentVO().apply(payment);
        paymentVO.getBuyer().setCpf("999999999999");

        assertBadRequest(getMockMvc().perform(MockMvcRequestBuilders.post(MessageFormat.format("/api/v1/{0}", BASEPATH))
                .content(getJson(paymentVO))
                .contentType("application/json")
                .accept(MediaType.APPLICATION_JSON)), PaymentMessages.Validations.BUYER_CPF_EMPTY_OR_INVALID.getCode());

    }

    @Test
    public void shouldFailWithInvalidBuyerEmail() throws Exception {

        PaymentVO paymentVO = new PaymentVO().apply(payment);
        paymentVO.getBuyer().setEmail("http://site");

        assertBadRequest(getMockMvc().perform(MockMvcRequestBuilders.post(MessageFormat.format("/api/v1/{0}", BASEPATH))
                .content(getJson(paymentVO))
                .contentType("application/json")
                .accept(MediaType.APPLICATION_JSON)), PaymentMessages.Validations.BUYER_EMAIL_EMPTY_OR_INVALID.getCode());

    }

    @Test
    public void shouldFailWithEmptyAmount() throws Exception {

        PaymentVO paymentVO = new PaymentVO().apply(payment);
        paymentVO.getPayment().setAmount(null);

        assertBadRequest(getMockMvc().perform(MockMvcRequestBuilders.post(MessageFormat.format("/api/v1/{0}", BASEPATH))
                .content(getJson(paymentVO))
                .contentType("application/json")
                .accept(MediaType.APPLICATION_JSON)), PaymentMessages.Validations.AMOUNT_IS_EMPTY.getCode());

    }

    @Test
    public void shouldFailWithEmptyPaymentDetails() throws Exception {

        PaymentVO paymentVO = new PaymentVO().apply(payment);
        paymentVO.setPayment(null);

        assertBadRequest(getMockMvc().perform(MockMvcRequestBuilders.post(MessageFormat.format("/api/v1/{0}", BASEPATH))
                .content(getJson(paymentVO))
                .contentType("application/json")
                .accept(MediaType.APPLICATION_JSON)), PaymentMessages.Validations.PAYMENT_IS_EMPTY.getCode());

    }

    @Test
    public void shouldCreatePaymentWithCreditCard() throws Exception {

        payment.setCard(CreditCard
                .builder()
                .number(CREDIT_CARD_NUMBER)
                .expirationDate(CREDIT_CARD_EXPIRATION)
                .holderName(CREDIT_CARD_HOLDER_NAME)
                .build());
        payment.setType(PaymentType.CREDIT_CARD);

        PaymentInputVO paymentVO = new PaymentInputVO().apply(payment);
        paymentVO.getPayment().getCard().setCvv(CREDIT_CARD_CVV);

        getMockMvc().perform(MockMvcRequestBuilders.post(MessageFormat.format("/api/v1/{0}", BASEPATH))
                .content(getJson(paymentVO))
                .contentType("application/json")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andDo(print())
                .andExpect(jsonPath("$.code", is(HttpStatus.CREATED.value())))
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.timestamp", greaterThan(1400000000)))
                .andExpect(jsonPath("$.result").isMap())
                .andExpect(jsonPath("$.result.guid", notNullValue()))

                .andExpect(jsonPath("$.result.buyer.name", is(BUYER_NAME)))
                .andExpect(jsonPath("$.result.buyer.email", is(BUYER_EMAIL)))
                .andExpect(jsonPath("$.result.buyer.cpf", is(Long.toString(BUYER_CPF))))

                .andExpect(jsonPath("$.result.payment.type", is(PaymentType.CREDIT_CARD.name())))
                .andExpect(jsonPath("$.result.payment.amount", is(payment.getAmount().doubleValue())))
                .andExpect(jsonPath("$.result.payment", not(hasProperty("boletoNumber"))))
                .andExpect(jsonPath("$.result.payment.card.holderName", is(CREDIT_CARD_HOLDER_NAME)))
                .andExpect(jsonPath("$.result.payment.card.number", is(CREDIT_CARD_NUMBER)))
                .andExpect(jsonPath("$.result.payment.card.expirationDate", is("01/2058")))
                .andExpect(jsonPath("$.result.payment.card.brand", is(CreditCardBrand.VISA.name())))
                .andExpect(jsonPath("$.result.status", is(PaymentStatus.PRE_AUTHORIZED.name())))
        ;

    }

    @Test
    public void shouldFailWithInvalidExpirationDate() throws Exception {

        payment.setCard(CreditCard
                .builder()
                .number(CREDIT_CARD_NUMBER)
                .expirationDate(CREDIT_CARD_EXPIRATION)
                .holderName(CREDIT_CARD_HOLDER_NAME)
                .build());
        payment.setType(PaymentType.CREDIT_CARD);

        PaymentInputVO paymentVO = new PaymentInputVO().apply(payment);
        paymentVO.getPayment().getCard().setCvv(CREDIT_CARD_CVV);
        paymentVO.getPayment().getCard().setExpirationDate("XX/XX");

        assertBadRequest(getMockMvc().perform(MockMvcRequestBuilders.post(MessageFormat.format("/api/v1/{0}", BASEPATH))
                .content(getJson(paymentVO))
                .contentType("application/json")
                .accept(MediaType.APPLICATION_JSON)), PaymentMessages.Validations.INVALID_EXPIRATION_DATE_FORMAT.getCode());

    }

    @Test
    public void shouldFailWithExpiredCard() throws Exception {

        payment.setCard(CreditCard
                .builder()
                .number(CREDIT_CARD_NUMBER)
                .expirationDate(CREDIT_CARD_EXPIRATION)
                .holderName(CREDIT_CARD_HOLDER_NAME)
                .build());
        payment.setType(PaymentType.CREDIT_CARD);

        PaymentInputVO paymentVO = new PaymentInputVO().apply(payment);
        paymentVO.getPayment().getCard().setCvv(CREDIT_CARD_CVV);
        paymentVO.getPayment().getCard().setExpirationDate("01/2000");

        getMockMvc().perform(MockMvcRequestBuilders.post(MessageFormat.format("/api/v1/{0}", BASEPATH))
                .content(getJson(paymentVO))
                .contentType("application/json")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andDo(print())
                .andExpect(jsonPath("$.code", is(HttpStatus.CREATED.value())))
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.timestamp", greaterThan(1400000000)))
                .andExpect(jsonPath("$.result").isMap())
                .andExpect(jsonPath("$.result.guid", notNullValue()))
                .andExpect(jsonPath("$.result.status", is(PaymentStatus.EXPIRED.name())))
        ;

    }

    @Test
    public void shouldFailWithBigAmount() throws Exception {

        payment.setCard(CreditCard
                .builder()
                .number(CREDIT_CARD_NUMBER)
                .expirationDate(CREDIT_CARD_EXPIRATION)
                .holderName(CREDIT_CARD_HOLDER_NAME)
                .build());
        payment.setType(PaymentType.CREDIT_CARD);

        PaymentInputVO paymentVO = new PaymentInputVO().apply(payment);
        paymentVO.getPayment().getCard().setCvv(CREDIT_CARD_CVV);
        paymentVO.getPayment().setAmount(BigDecimal.valueOf(900000.0));

        getMockMvc().perform(MockMvcRequestBuilders.post(MessageFormat.format("/api/v1/{0}", BASEPATH))
                .content(getJson(paymentVO))
                .contentType("application/json")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andDo(print())
                .andExpect(jsonPath("$.code", is(HttpStatus.CREATED.value())))
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.timestamp", greaterThan(1400000000)))
                .andExpect(jsonPath("$.result").isMap())
                .andExpect(jsonPath("$.result.guid", notNullValue()))
                .andExpect(jsonPath("$.result.status", is(PaymentStatus.REFUSED.name())))
        ;

    }

    @Test
    public void shouldFailWithInvalidCreditCardNumber() throws Exception {

        payment.setCard(CreditCard
                .builder()
                .number(0L)
                .expirationDate(CREDIT_CARD_EXPIRATION)
                .holderName(CREDIT_CARD_HOLDER_NAME)
                .build());
        payment.setType(PaymentType.CREDIT_CARD);

        PaymentInputVO paymentVO = new PaymentInputVO().apply(payment);
        paymentVO.getPayment().getCard().setCvv(CREDIT_CARD_CVV);

        assertBadRequest(getMockMvc().perform(MockMvcRequestBuilders.post(MessageFormat.format("/api/v1/{0}", BASEPATH))
                .content(getJson(paymentVO))
                .contentType("application/json")
                .accept(MediaType.APPLICATION_JSON)), PaymentMessages.Validations.CREDIT_CARD_NUMBER_EMPTY_OR_INVALID.getCode());

    }

    @Test
    public void shouldFailWithEmptyCreditCardHolder() throws Exception {

        payment.setCard(CreditCard
                .builder()
                .number(CREDIT_CARD_NUMBER)
                .expirationDate(CREDIT_CARD_EXPIRATION)
                .holderName(CREDIT_CARD_HOLDER_NAME)
                .build());
        payment.setType(PaymentType.CREDIT_CARD);

        PaymentInputVO paymentVO = new PaymentInputVO().apply(payment);
        paymentVO.getPayment().getCard().setHolderName("");

        assertBadRequest(getMockMvc().perform(MockMvcRequestBuilders.post(MessageFormat.format("/api/v1/{0}", BASEPATH))
                .content(getJson(paymentVO))
                .contentType("application/json")
                .accept(MediaType.APPLICATION_JSON)), PaymentMessages.Validations.CREDIT_CARD_HOLDER_NAME_EMPTY.getCode());

    }

    @Test
    public void shouldFailWithNullExpirationDate() throws Exception {

        payment.setCard(CreditCard
                .builder()
                .number(CREDIT_CARD_NUMBER)
                .expirationDate(CREDIT_CARD_EXPIRATION)
                .holderName(CREDIT_CARD_HOLDER_NAME)
                .build());
        payment.setType(PaymentType.CREDIT_CARD);

        PaymentVO paymentVO = new PaymentVO().apply(payment);
        paymentVO.getPayment().getCard().setExpirationDate(null);

        assertBadRequest(getMockMvc().perform(MockMvcRequestBuilders.post(MessageFormat.format("/api/v1/{0}", BASEPATH))
                .content(getJson(paymentVO))
                .contentType("application/json")
                .accept(MediaType.APPLICATION_JSON)), PaymentMessages.Validations.CREDIT_CARD_EXPIRATION_EMPTY.getCode());

    }

    private void assertBadRequest(ResultActions perform, String code) throws Exception {

        perform.andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andDo(print())
                .andExpect(jsonPath("$.code", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.timestamp", greaterThan(1400000000)))
                .andExpect(jsonPath("$.messages").isArray())
                .andExpect(jsonPath("$.messages[0].code", is(code)));

    }

    @Test
    public void shouldFailWithInvalidPaymentType() throws Exception {

        PaymentVO paymentVO = new PaymentVO().apply(payment);
        paymentVO.getPayment().setType("CASH");

        assertBadRequest(getMockMvc().perform(MockMvcRequestBuilders.post(MessageFormat.format("/api/v1/{0}", BASEPATH))
                .content(getJson(paymentVO))
                .contentType("application/json")
                .accept(MediaType.APPLICATION_JSON)), PaymentMessages.Validations.PAYMENT_TYPE_IS_EMPTY_OR_INVALID.getCode());

    }

    @Test
    public void shouldReadPayment() throws Exception {

        getMockMvc().perform(MockMvcRequestBuilders.get(MessageFormat.format("/api/v1/{0}/{1}", BASEPATH, GUID))
                .contentType("application/json")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.code", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.timestamp", greaterThan(1400000000)))
                .andExpect(jsonPath("$.result").isMap())
                .andExpect(jsonPath("$.result.guid", is(GUID)))
                .andExpect(jsonPath("$.result.buyer.name", is(BUYER_NAME)))
                .andExpect(jsonPath("$.result.buyer.email", is(BUYER_EMAIL)))
                .andExpect(jsonPath("$.result.buyer.cpf", is(Long.toString(BUYER_CPF))))
        ;

    }

    @Test
    public void shouldFailReadNonExistingPayment() throws Exception {

        getMockMvc().perform(MockMvcRequestBuilders.get(MessageFormat.format("/api/v1/{0}/{1}", BASEPATH, NON_EXISTING_GUID))
                .contentType("application/json")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.code", is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.timestamp", greaterThan(1400000000)))
        ;

    }

}
