package co.com.pragma.usecase.requests.validations;

import co.com.pragma.model.requests.Requests;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class RequestsValidation {
    private final List<IRequestsValidation> validations = new ArrayList<>();

    public RequestsValidation includeValidation(IRequestsValidation validation) {
        validations.add(validation);
        return this;
    }

    public Mono<Void> validate(Requests requests) {
        Mono<Void> result = Mono.empty();
        for (IRequestsValidation validation : validations) {
            result = result.then(validation.validate(requests));
        }
        return result;
    }
}
