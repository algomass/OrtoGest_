package it.ortogest.ortogestapp.exception;

public class ValidationException extends GestioneException {
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
