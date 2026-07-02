package it.ortogest.ortogestapp.exception;

public class GestioneException extends Exception {
    public GestioneException(String message) {
        super(message);
    }

    public GestioneException(String message, Throwable cause) {
        super(message, cause);
    }
}
