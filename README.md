# Checkout Challenge

A fictitious company called ACME needs a checkout-payment API.

## Technology Stack

* SpringBoot 2.0
* MongoDB
* Swagger for API Documentation
* JUnit for testing

## Initial requirements

* Send the buyer information with the payment method.
* Validate if the card is valid and who is the card issuer.
* Simulate a form of identification of the buyer that will be send to the API.
* Return if the transaction was successful or not.
* Persist and consume the data effectively.

## Additional requirements

* Do not store the CVV value. It is a sensitive data.
* Credit card payments greater than 5,000.00 will be refused.
* Payments with expired credit card will be refused.
* The user can only see his own payments.

## Usage Instructions

### Prerequisites

* Docker 1.12+
* Docker Compose 1.12+
* Java 10
* Maven

### Package

```
mvn package
```

### Run

```
docker-compose up --build
```

## Test API

### Access the documentation page

```
http://localhost:8080/swagger-ui.html
```

### Authentication

* Click on 'Authorize' button
* Enter any valid email. This will simulate the identification of the buyer that will be send to the API
* Click on 'Authorize' button
* Click on 'Close' button

### API Calls

Use the Swagger page to make the API calls to the APIs.

* Select the endpoint
* Click on 'Try it out'
* Change the 'Parameters' with your test case
* Click on 'Execute'
* The API response will be shown bellow with a ```curl``` example

If you are new to Swagger, this article should help: [How to Use Swagger UI for API Testing](https://dzone.com/articles/how-to-use-swagger-ui-for-api-testing)

## Architecture

This is an application with a monolithic layered architecture:

![Pattern](images/design-patterns.png?raw=true "Title")

## Future Improvements

* Make the calls to credit card service reactive