package co.com.pragma.usecase.requests;

import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.model.requests.Requests;
import co.com.pragma.model.requests.gateways.RequestsRepository;
import co.com.pragma.model.status.gateways.StatusRepository;
import co.com.pragma.usecase.requests.validations.RequestsValidation;
import co.com.pragma.usecase.requests.validations.cases.AmountAndTermValidation;
import co.com.pragma.usecase.requests.validations.cases.EmailValidation;
import co.com.pragma.usecase.requests.validations.cases.RequestsDataValidation;
import co.com.pragma.usecase.requests.validations.cases.UserExistValidation;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RequestsUseCase implements RequestsUseCaseInterface {

    private final LoanTypeRepository loanTypeRepository;
    private final StatusRepository statusRepository;
    private final RequestsRepository requestsRepository;
    private final UserUseCaseInterface userUseCaseInterface;

    @Override
    public Mono<Requests> saveRequests(Requests requests) {
        RequestsValidation requestsValidation = new RequestsValidation()
                .includeValidation(new AmountAndTermValidation())
                .includeValidation(new RequestsDataValidation())
                .includeValidation(new EmailValidation())
                .includeValidation(new UserExistValidation(userUseCaseInterface));

        return requestsValidation.validate(requests)
                .then(loanTypeRepository.findLoanTypeById(requests.getLoanTypeId())
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("The loan type Id does not exists.")))
                        .flatMap(loanType -> {
                            requests.setLoanTypeId(loanType.getId());
                            return statusRepository.findStatusById(4L) // Se busca si existe el status en la tabla correspondiente
                                    .flatMap(status -> {
                                        status.setId(4L); // Se almacena un status por defecto
                                        requests.setStatusId(status.getId());
                                        return userUseCaseInterface.isValidUser(requests.getIdentityNumber(), requests.getEmail())
                                                .flatMap(isValid -> {
                                                    if (Boolean.FALSE.equals(isValid)) {
                                                        return Mono.error(new RuntimeException("User is not valid"));
                                                    }
                                                    Requests requestValidated = requests;
                                                    return requestsRepository.saveRequests(requestValidated);
                                                });
                                    });
                        }).log()
                );
    }

    @Override
    public Flux<Requests> findAllRequests() {
        return requestsRepository.findAllRequests();
    }

    @Override
    public Mono<Requests> getRequestsByIdentityNumber(String identityNumber) {
        return requestsRepository.findByIdentityNumber(identityNumber);
    }

    @Override
    public Mono<Requests> getRequestsByEmail(String email) {
        return requestsRepository.findByEmail(email);
    }
}
