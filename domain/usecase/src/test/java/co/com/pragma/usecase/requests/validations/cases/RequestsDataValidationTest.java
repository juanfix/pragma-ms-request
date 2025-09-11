package co.com.pragma.usecase.requests.validations.cases;

import co.com.pragma.model.requests.Requests;
import co.com.pragma.usecase.requests.validations.error.RequestsValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class RequestsDataValidationTest {
    private RequestsDataValidation requestsDataValidation;

    @BeforeEach
    void setUp() {
        requestsDataValidation = new RequestsDataValidation();
    }

    @Test
    void shouldPassWhenAllDataIsValid() {
        Requests requests = new Requests();
        requests.setIdentityNumber("123");
        requests.setEmail("juan@mail.com");
        requests.setAmount(1000000L);
        requests.setTerm(12);

        StepVerifier.create(requestsDataValidation.validate(requests))
                .verifyComplete();
    }

    @Test
    void shouldFailWhenIdentityNumberOrEmailIsNull() {
        Requests requests = new Requests();
        requests.setIdentityNumber("");
        requests.setEmail("");
        requests.setAmount(1000000L);
        requests.setTerm(12);

        StepVerifier.create(requestsDataValidation.validate(requests))
                .expectError(RequestsValidationException.class)
                .verify();
    }

    @Test
    void shouldFailWhenAmountIsNull() {
        Requests requests = new Requests();
        requests.setIdentityNumber("123");
        requests.setEmail("juan@mail.com");
        requests.setAmount(null);
        requests.setTerm(12);

        StepVerifier.create(requestsDataValidation.validate(requests))
                .expectError(RequestsValidationException.class)
                .verify();
    }

    @Test
    void shouldFailWhenTermIsNull() {
        Requests requests = new Requests();
        requests.setIdentityNumber("123");
        requests.setEmail("juan@mail.com");
        requests.setAmount(1000000L);
        requests.setTerm(null);

        StepVerifier.create(requestsDataValidation.validate(requests))
                .expectError(RequestsValidationException.class)
                .verify();
    }

}
