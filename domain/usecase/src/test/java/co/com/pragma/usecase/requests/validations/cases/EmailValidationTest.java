package co.com.pragma.usecase.requests.validations.cases;

import co.com.pragma.model.requests.Requests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class EmailValidationTest {
    private EmailValidation emailValidation;

    @BeforeEach
    void setUp() {
        emailValidation = new EmailValidation();
    }

    @Test
    void shouldFailWhenEmailIsEmpty() {
        Requests requests = Requests.builder().email("").build();

        StepVerifier.create(emailValidation.validate(requests))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().contains("El campo email es obligatorio."))
                .verify();
    }

    @Test
    void shouldFailWhenEmailIsNull() {
        Requests requests = Requests.builder().email(null).build();

        StepVerifier.create(emailValidation.validate(requests))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().contains("El campo email es obligatorio."))
                .verify();
    }

    @Test
    void shouldFailWhenEmailFormatIsInvalid() {
        Requests requests = Requests.builder().email("hola").build();

        StepVerifier.create(emailValidation.validate(requests))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().contains("El campo email debe tener un formato válido."))
                .verify();
    }

}
