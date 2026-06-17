package it.ortogest.ortogestapp.pattern;

import it.ortogest.ortogestapp.utils.Printer;

/**
 * Implementazione concreta (Simulatore) dell'EmailAdapter.
 * Stampa l'email nella console invece di inviarla realmente tramite rete.
 */
public class DummyEmailService implements EmailTarget {

    @Override
    public boolean inviaEmail(String destinatario, String oggetto, String corpo) {
        Printer.printf("\n==================================================");
        Printer.printf("            SIMULAZIONE INVIO EMAIL             ");
        Printer.printf("==================================================");
        Printer.printf("A: " + destinatario);
        Printer.printf("Oggetto: " + oggetto);
        Printer.printf("--------------------------------------------------");
        Printer.printf(corpo);
        Printer.printf("==================================================\n");

        // Simuliamo che vada sempre a buon fine
        return true;
    }
}
