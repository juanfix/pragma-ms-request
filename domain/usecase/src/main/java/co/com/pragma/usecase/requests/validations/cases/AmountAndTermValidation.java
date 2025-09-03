package co.com.pragma.usecase.requests.validations.cases;

import co.com.pragma.model.requests.Requests;
import co.com.pragma.usecase.requests.validations.IRequestsValidation;
import reactor.core.publisher.Mono;

public class AmountAndTermValidation implements IRequestsValidation {
    @Override
    public Mono<Void> validate(Requests requests) {
        if(requests.getAmount() <= 0){
            return Mono.error(new IllegalArgumentException("The amount must be 1 or greater."));
        }
        if(requests.getTerm() <= 0){
            return Mono.error(new IllegalArgumentException("The term must be 1 or greater."));
        }
        return Mono.empty();
    }
}
