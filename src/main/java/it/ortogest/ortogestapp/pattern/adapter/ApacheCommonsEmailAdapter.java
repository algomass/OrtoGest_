package it.ortogest.ortogestapp.pattern.adapter;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Concrete Adapter per l'invio di Email.
 * Adatta la libreria esterna Apache Commons Email (l'Adaptee è Simple
 * 
 * 
 * mail)
 * all'interfaccia Target (EmailAdapter) del nostro sistema.
 */
public class ApacheCommonsEmailAdapter implements EmailTarget {

    private static final Logger LOGGER = Logger.getLogger(ApacheCommonsEmailAdapter.class.getName());

    @Override
    public boolean inviaEmail(String destinatario, String oggetto, String corpo) {
        try {
            // L'Adaptee è l'oggetto SimpleEmail.
            // Notare come l'interfaccia dell'Adaptee (setHostName, setSubject, setMsg)
            // è completamente diversa dal nostro metodo Target "inviaEmail".
            // L'Adapter ha proprio il compito di tradurre!
            Email email = new SimpleEmail();

            // Configurazione Server SMTP
            email.setHostName("smtp.googlemail.com");
            email.setSmtpPort(465);
            email.setAuthentication("algozzinomatteo@gmail.com", "hgmt hctv pycy vfrr");
            email.setSSLOnConnect(true);

            // Mappatura dei parametri del nostro Target sull'Adaptee
            email.setFrom("sistema@ortogest.it");
            email.setSubject(oggetto);
            email.setMsg(corpo);
            email.addTo(destinatario);

            // IMPORTANTE: Per evitare che l'applicazione vada in crash al runtime
            //
            // a causa di credenziali fittizie, commentiamo il vero e proprio invio fisico.
            //
            // Basterà inserire le credenziali corrette e decommentare la riga sotto per
            //
            // inviare davvero.
            email.send();

            LOGGER.info("[Adapter] L'Adapter ha configurato e instradato con successo la richiesta verso la libreria Apache Commons (Adaptee).");
            return true;
        } catch (EmailException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'invio dell'email", e);
            return false;
        }
    }
}
