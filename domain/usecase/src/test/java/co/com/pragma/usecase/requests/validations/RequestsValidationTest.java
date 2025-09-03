package co.com.pragma.usecase.requests.validations;

import co.com.pragma.model.requests.Requests;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class RequestsValidationTest {
    @Test
    void shouldBeCalled() {
        IRequestsValidation validation = mock(IRequestsValidation.class);

        when(validation.validate(any())).thenReturn(Mono.empty());

        RequestsValidation requestsValidation = new RequestsValidation()
                .includeValidation(validation);

        StepVerifier.create(requestsValidation.validate(new Requests()))
                .verifyComplete();

        verify(validation).validate(any());
    }

    @Test
    void shouldFailWhenAtLeastOneValidationReturnsAnException() {
        IRequestsValidation validationSuccess = mock(IRequestsValidation.class);
        IRequestsValidation validationFail = mock(IRequestsValidation.class);

        when(validationSuccess.validate(any())).thenReturn(Mono.empty());
        when(validationFail.validate(any())).thenReturn(Mono.error(new IllegalArgumentException("Any error message")));

        RequestsValidation requestsValidation = new RequestsValidation()
                .includeValidation(validationSuccess)
                .includeValidation(validationFail);

        StepVerifier.create(requestsValidation.validate(new Requests()))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException&&
                        throwable.getMessage().equals("Any error message"))
                .verify();

        verify(validationSuccess).validate(any());
        verify(validationFail).validate(any());
    }
}
