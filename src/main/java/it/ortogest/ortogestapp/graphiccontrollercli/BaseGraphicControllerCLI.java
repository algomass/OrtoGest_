package it.ortogest.ortogestapp.graphiccontrollercli;

import java.util.Scanner;
import it.ortogest.ortogestapp.utils.Printer;
import it.ortogest.ortogestapp.utils.SessionManager;

/**
 * Classe base astratta per tutti i controller grafici CLI.
 * Centralizza l'acquisizione robusta dell'input e le funzionalità comuni (es. logout).
 */
public abstract class BaseGraphicControllerCLI implements GraphicControllerCLI {

    /**
     * Legge un intero da terminale gestendo i NumberFormatException.
     * Continua a chiedere finché l'utente non inserisce un numero compreso tra min e max.
     */
    protected int leggiInteroValido(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            Printer.print(prompt);
            try {
                int scelta = Integer.parseInt(scanner.nextLine().trim());
                if (scelta >= min && scelta <= max) {
                    return scelta;
                } else {
                    Printer.perror("Errore: la scelta deve essere compresa tra " + min + " e " + max + ".");
                }
            } catch (NumberFormatException e) {
                it.ortogest.ortogestapp.exception.ValidationException ve = new it.ortogest.ortogestapp.exception.ValidationException("inserire un numero intero valido.", e);
                Printer.perror("Errore: " + ve.getMessage());
            }
        }
    }

    /**
     * Legge un double da terminale gestendo i NumberFormatException.
     * Continua a chiedere finché l'utente non inserisce un numero maggiore o uguale a min.
     */
    protected double leggiDoubleValido(Scanner scanner, String prompt, double min) {
        while (true) {
            Printer.print(prompt);
            try {
                double val = Double.parseDouble(scanner.nextLine().trim());
                if (val >= min) {
                    return val;
                } else {
                    Printer.perror("Errore: il valore deve essere maggiore o uguale a " + min + ".");
                }
            } catch (NumberFormatException e) {
                it.ortogest.ortogestapp.exception.ValidationException ve = new it.ortogest.ortogestapp.exception.ValidationException("inserire un numero decimale valido (es. 2.50).", e);
                Printer.perror("Errore: " + ve.getMessage());
            }
        }
    }

    /**
     * Legge una stringa non vuota.
     */
    protected String leggiStringaNonVuota(Scanner scanner, String prompt) {
        while (true) {
            Printer.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            Printer.perror("Errore: il campo non può essere vuoto.");
        }
    }

    /**
     * Legge una stringa opzionale (può essere vuota).
     */
    protected String leggiStringaOpzionale(Scanner scanner, String prompt) {
        Printer.print(prompt);
        return scanner.nextLine().trim();
    }

    /**
     * Esegue il logout dell'utente corrente e pulisce il carrello se esistente.
     */
    protected void eseguiLogout() {
        Printer.print("Logout in corso...");
        SessionManager.getInstance().setCarrelloCorrente(null);
        SessionManager.getInstance().logout();
    }

    /**
     * Stampa un menu standardizzato.
     */
    protected void stampaMenu(String titolo, String... opzioni) {
        Printer.print("\n=================================");
        Printer.print("       " + titolo + "       ");
        Printer.print("=================================");
        for (String opzione : opzioni) {
            Printer.print(opzione);
        }
    }
}
