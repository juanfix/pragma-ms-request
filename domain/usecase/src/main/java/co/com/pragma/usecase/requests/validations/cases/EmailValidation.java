package co.com.pragma.usecase.requests.validations.cases;

import co.com.pragma.model.requests.Requests;
import co.com.pragma.usecase.requests.validations.IRequestsValidation;
import co.com.pragma.usecase.requests.validations.error.RequestsValidationException;
import reactor.core.publisher.Mono;

public class EmailValidation implements IRequestsValidation {

    @Override
    public Mono<Void> validate(Requests requests) {
        if (requests.getEmail() == null || requests.getEmail().isEmpty()) {
            return Mono.error(new RequestsValidationException("El campo email es obligatorio."));
        }
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!requests.getEmail().matches(emailRegex)) {
            return Mono.error(new RequestsValidationException("El campo email debe tener un formato válido."));
        }

        return Mono.empty();
    }
}
