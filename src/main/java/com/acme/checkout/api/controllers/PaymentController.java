package com.acme.checkout.api.controllers;

import com.acme.checkout.api.model.PaymentDetailsInputVO;
import com.acme.checkout.exceptions.ValidationException;
import com.acme.checkout.exceptions.NotAuthorizedException;
import com.acme.checkout.exceptions.NotFoundException;
import com.acme.checkout.api.model.CreditCardVO;
import com.acme.checkout.api.model.PaymentInputVO;
import com.acme.checkout.api.model.PaymentVO;
import com.acme.checkout.domain.model.CreditCard;
import com.acme.checkout.domain.model.PaymentType;
import com.acme.checkout.service.PaymentService;
import com.acme.checkout.domain.model.Payment;
import com.acme.checkout.domain.model.User;
import com.acme.checkout.service.messages.PaymentMessages;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(
        value = PaymentController.URL
)
@Api(tags="payments")
public class PaymentController {

    private final PaymentService paymentService;

    public static final String URL = "/api/v1/payments";

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @ApiOperation(value="create a payment", notes=
        "* The API accepts two payment methods: Credit Card and Boleto.\n" +
        "* When the payment method is boleto, returns the boleto's number in the response."
    )
    public PaymentVO create(@RequestBody PaymentInputVO paymentForm)
            throws ValidationException, NotAuthorizedException, NotFoundException {

        User user = getUser();

        List<String> validationsErrors = validate(paymentForm);
        if (!validationsErrors.isEmpty()) {
            throw new ValidationException(validationsErrors);
        }

        Payment payment = Payment.builder().build();

        payment.setAmount(paymentForm.getPayment().getAmount());
        payment.setType(PaymentType.fromString(paymentForm.getPayment().getType()));

        payment.setName(paymentForm.getBuyer().getName());
        payment.setCpf(parseLongString(paymentForm.getBuyer().getCpf()));
        payment.setEmail(paymentForm.getBuyer().getEmail());

        if (payment.getType() == null) {
            throw new ValidationException(PaymentMessages.Validations.PAYMENT_TYPE_IS_EMPTY_OR_INVALID.getCode());
        }

        Integer cvv = null;
        switch (payment.getType()) {
            case BOLETO:
                break;
            case CREDIT_CARD:
                payment.setCard(parseCreditCard(paymentForm.getPayment().getCard()));
                cvv = paymentForm.getPayment().getCard().getCvv();
                break;
            default:
                throw new ValidationException(PaymentMessages.Validations.PAYMENT_TYPE_NOT_SUPPORTED.getCode());
        }

        Payment paymentPersisted = paymentService.create(user.getGuid(), payment, cvv);

        return new PaymentVO().apply(paymentPersisted);

    }

    private List<String> validate(PaymentInputVO paymentForm) {

        List<String> validations = new ArrayList<>();

        if (paymentForm.getBuyer() == null) {
            validations.add(PaymentMessages.Validations.BUYER_IS_EMPTY.getCode());
        }

        if (paymentForm.getPayment() == null) {
            validations.add(PaymentMessages.Validations.PAYMENT_IS_EMPTY.getCode());
        } else {
            PaymentDetailsInputVO paymentDetails = paymentForm.getPayment();
            if (paymentDetails.getAmount() == null) {
                validations.add(PaymentMessages.Validations.AMOUNT_IS_EMPTY.getCode());
            }
        }

        return validations;

    }

    @GetMapping(path = "/{paymentGuid}")
    @ApiOperation(value="find a payment", notes=
            "* Returns all the information about the payment, as well as the status of that payment."
    )
    public PaymentVO read(
            @PathVariable("paymentGuid") String paymentGuid)
            throws NotFoundException, NotAuthorizedException {

        User user = getUser();

        Payment payment = paymentService.findByGuid(user.getGuid(), paymentGuid);
        if (payment == null) {
            throw new NotFoundException(PaymentMessages.Validations.PAYMENT_NOT_FOUND.getCode());
        }

        return new PaymentVO().apply(payment);
    }

    private Long parseLongString(String text) {
        return text == null ? null : Long.parseLong(text.replaceAll("[^\\d]", ""));
    }

    protected User getUser() throws NotAuthorizedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (Optional.ofNullable(authentication).isPresent() &&
                User.class.isInstance(authentication.getPrincipal())) {
            User user = (User) authentication.getPrincipal();
            return user;
        } else {
            throw new NotAuthorizedException();
        }
    }

    public CreditCard parseCreditCard(CreditCardVO creditCardVO) throws ValidationException {
        CreditCard card = new CreditCard();

        card.setHolderName(creditCardVO.getHolderName());
        card.setNumber(creditCardVO.getNumber());
        card.setExpirationDate(parseExpirationDate(creditCardVO.getExpirationDate()));

        return card;
    }

    private LocalDate parseExpirationDate(String expirationDate) throws ValidationException {

        if (expirationDate == null){
            throw new ValidationException(PaymentMessages.Validations.CREDIT_CARD_EXPIRATION_EMPTY.getCode());
        }
        if (!expirationDate.matches("(?:0[1-9]|1[0-2])/20[0-9]{2}")) {
            throw new ValidationException(PaymentMessages.Validations.INVALID_EXPIRATION_DATE_FORMAT.getCode());
        }

        String tokens[] = expirationDate.split("/");
        int year = Integer.parseInt(tokens[1]);
        int month = Integer.parseInt(tokens[0]);

        LocalDate date = YearMonth.of(year, month).atEndOfMonth();
        return date;

    }

}