package it.ortogest.ortogestapp.pattern;

/**
 * Interfaccia Target del pattern Adapter per l'invio di Email.
 * Isola l'Application Controller dai dettagli tecnici di implementazione di API
 * esterne.
 */
public interface EmailTarget {
    /**
     * Invia un'email a un fornitore.
     * 
     * @param destinatario L'indirizzo email del fornitore.
     * @param oggetto      L'oggetto dell'email.
     * @param corpo        Il testo del messaggio.
     * @return true se l'invio ha successo, false altrimenti.
     */
    boolean inviaEmail(String destinatario, String oggetto, String corpo);
}
