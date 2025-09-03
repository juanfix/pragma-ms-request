package co.com.pragma.usecase.requests.validations.cases;

import co.com.pragma.model.requests.Requests;
import co.com.pragma.usecase.requests.UserUseCaseInterface;
import co.com.pragma.usecase.requests.validations.IRequestsValidation;
import reactor.core.publisher.Mono;

public class UserExistValidation implements IRequestsValidation {

    private final UserUseCaseInterface userUseCaseInterface;

    public UserExistValidation(UserUseCaseInterface userUseCaseInterface) {
        this.userUseCaseInterface = userUseCaseInterface;
    }

    @Override
    public Mono<Void> validate(Requests requests) {

        return userUseCaseInterface.isValidUser(requests.getIdentityNumber(), requests.getEmail())
                .flatMap(isValid -> {
                    if (Boolean.FALSE.equals(isValid)) {
                        return Mono.error(new IllegalArgumentException("Usuario no válido"));
                    }
                    return Mono.empty();
                });
    }
}
