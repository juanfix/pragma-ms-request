package co.com.pragma.usecase.requests.validations.cases;

import co.com.pragma.model.requests.Requests;
import co.com.pragma.usecase.requests.UserUseCaseInterface;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class UserExistValidationTest {
    @Test
    void shouldFailWhenUserIsNotValid() {
        Requests request = new Requests().toBuilder()
                .amount(1000000L)
                .term(6)
                .email("mariacecilia@gmail.com")
                .identityNumber("123456")
                .loanTypeId(1L)
                .build();

        UserUseCaseInterface userUseCaseInterface = mock(UserUseCaseInterface.class);

        when(userUseCaseInterface.isValidUser(request.getIdentityNumber(), request.getEmail()))
                .thenReturn(Mono.just(false));

        UserExistValidation validation = new UserExistValidation(userUseCaseInterface);

        StepVerifier.create(validation.validate(request))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void shouldPassWhenUserIsValid() {

        Requests request = new Requests().toBuilder()
                .amount(1000000L)
                .term(6)
                .email("mariacecilia@gmail.com")
                .identityNumber("123456")
                .loanTypeId(1L)
                .build();

        UserUseCaseInterface userUseCaseInterface = mock(UserUseCaseInterface.class);

        when(userUseCaseInterface.isValidUser(request.getIdentityNumber(), request.getEmail()))
                .thenReturn(Mono.just(true));

        UserExistValidation validation = new UserExistValidation(userUseCaseInterface);

        StepVerifier.create(validation.validate(request))
                .verifyComplete();
    }

}
