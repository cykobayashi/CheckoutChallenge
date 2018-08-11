package com.acme.checkout.api;

import com.acme.checkout.api.model.ResponseMessage;
import com.acme.checkout.api.model.RestResponseBuilder;
import com.acme.checkout.exceptions.NotAuthorizedException;
import com.acme.checkout.exceptions.NotFoundException;
import com.acme.checkout.exceptions.UnexpectedException;
import com.acme.checkout.exceptions.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@ControllerAdvice(basePackages = "com.acme.checkout.api.controllers")
@Order(1)
public class CrudResponseAdvice implements ResponseBodyAdvice<Object> {

    private final MessageSource messageSource;

    @Autowired
    public CrudResponseAdvice(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
                                  ServerHttpResponse response) {

        HttpStatus httpStatus = HttpStatus.OK;

        if (request.getMethod().equals(HttpMethod.DELETE)) {
            httpStatus = HttpStatus.NO_CONTENT;
        } else if (request.getMethod().equals(HttpMethod.POST)) {
            httpStatus = HttpStatus.CREATED;
        } else if (request.getMethod().equals(HttpMethod.PUT)) {
            httpStatus = HttpStatus.OK;
        }

        response.setStatusCode(httpStatus);
        return RestResponseBuilder.ok().withHttpStatus(httpStatus).withResult(body).getResponse();

    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> exception(NotFoundException e) {
        return RestResponseBuilder.error().withHttpStatus(HttpStatus.NOT_FOUND).withMessages(getI18NMessages(e.getMessagesCode())).build();
    }

    @ExceptionHandler(NotAuthorizedException.class)
    public ResponseEntity<?> exception(NotAuthorizedException e) {
        return RestResponseBuilder.error()
                .withHttpStatus(HttpStatus.FORBIDDEN)
                .withMessage(ResponseMessage.builder().description(e.getMessage()).build()).build();
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> exception(ValidationException e) {

        return RestResponseBuilder.error()
                .withHttpStatus(HttpStatus.BAD_REQUEST)
                .withMessages(getI18NMessages(e.getMessagesCode())).build();

    }

    @ExceptionHandler(UnexpectedException.class)
    public ResponseEntity<?> exception(UnexpectedException e) {

        return RestResponseBuilder.error()
                .withHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();

    }

    private final Locale localeEnUs = new Locale("en", "US");

    private List<ResponseMessage> getI18NMessages(List<String> messagesCode) {

        List<ResponseMessage> messages = new ArrayList<>();

        if (messagesCode != null) {
            for (String code : messagesCode) {
                ResponseMessage responseMessage = ResponseMessage.builder()
                        .code(code)
                        .description(messageSource.getMessage(code, null, localeEnUs))
                        .build();

                messages.add(responseMessage);
            }
        }

        return messages;

    }

}