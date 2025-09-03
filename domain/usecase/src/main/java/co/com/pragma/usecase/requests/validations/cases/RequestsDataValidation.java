package co.com.pragma.usecase.requests.validations.cases;

import co.com.pragma.model.requests.Requests;
import co.com.pragma.usecase.requests.validations.IRequestsValidation;
import reactor.core.publisher.Mono;

public class RequestsDataValidation implements IRequestsValidation {

    @Override
    public Mono<Void> validate(Requests requests) {
        if(requests.getIdentityNumber().isEmpty() || requests.getEmail().isEmpty()){
            return Mono.error(new IllegalArgumentException("The user data is not valid."));
        }
        if (requests.getAmount() == null) {
            return Mono.error(new IllegalArgumentException("Amount is required."));
        }
        if (requests.getTerm() == null) {
            return Mono.error(new IllegalArgumentException("The term of the load is required."));
        }
        return Mono.empty();
    }
}
