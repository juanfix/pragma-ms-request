package co.com.pragma.usecase.requests.validations.cases;

import co.com.pragma.model.requests.Requests;
import co.com.pragma.model.requests.gateways.RequestsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AmountAndTermValidationTest {
    private RequestsRepository requestsRepository;
    private AmountAndTermValidation amountAndTermValidation;

    @BeforeEach
    void setUp() {
        requestsRepository = mock(RequestsRepository.class);
        amountAndTermValidation = new AmountAndTermValidation();
    }

    @Test
    void shouldPassWhenAmountAndTermIsGreaterThan0() {
        Requests requests = Requests.builder().build();
        requests.setAmount(5000000L);
        requests.setTerm(5);

        StepVerifier.create(amountAndTermValidation.validate(requests))
                .verifyComplete();
    }

    @Test
    void shouldFailsWhenAmountIsNotGreaterThan0() {
        Requests requests = Requests.builder().build();
        requests.setAmount(-26L);
        requests.setTerm(3);

        StepVerifier.create(amountAndTermValidation.validate(requests))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().contains("The amount must be 1 or greater."))
                .verify();
    }

    @Test
    void shouldFailsWhenTermIsNotGreaterThan0() {
        Requests requests = Requests.builder().build();
        requests.setAmount(2000000L);
        requests.setTerm(-3);

        StepVerifier.create(amountAndTermValidation.validate(requests))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().contains("The term must be 1 or greater."))
                .verify();
    }

}
