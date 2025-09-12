package co.com.pragma.usecase.requests.interfaces;

import co.com.pragma.usecase.requests.dto.UserSalaryInformationDTO;
import reactor.core.publisher.Mono;

public interface UserUseCaseInterface {
    Mono<Boolean> isValidUser(String identityNumber, String email);
    Mono<UserSalaryInformationDTO> getUserSalaryInformation(String identityNumber);
}
