package it.ortogest.ortogestapp.exception;

/**
 * Eccezione custom lanciata quando il login fallisce (es. credenziali errate).
 */
public class LoginFallitoException extends Exception {
    public LoginFallitoException(String message) {
        super(message);
    }
}
