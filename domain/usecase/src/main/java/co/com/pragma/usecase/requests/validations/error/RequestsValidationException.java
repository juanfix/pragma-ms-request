package co.com.pragma.usecase.requests.validations.error;

public class RequestsValidationException extends RuntimeException{
    public RequestsValidationException(String message) {
        super(message);
    }
}
