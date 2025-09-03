package co.com.pragma.usecase.requests;

import reactor.core.publisher.Mono;

public interface UserUseCaseInterface {
    Mono<Boolean> isValidUser(String identityNumber, String email);
}
