package it.ortogest.ortogestapp.exception;

public class ViewException extends Exception {
    public ViewException(String message) {
        super(message);
    }

    public ViewException(String message, Throwable cause) {
        super(message, cause);
    }
}
