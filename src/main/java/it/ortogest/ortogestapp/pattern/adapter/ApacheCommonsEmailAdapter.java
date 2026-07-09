package it.ortogest.ortogestapp.pattern.adapter;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ApacheCommonsEmailAdapter implements EmailTarget {

    private static final Logger LOGGER = Logger.getLogger(ApacheCommonsEmailAdapter.class.getName());

    @Override
    public boolean inviaEmail(String destinatario, String oggetto, String corpo) {
        try {
            
            
            
            
            Email email = new SimpleEmail();

            
            email.setHostName("smtp.googlemail.com");
            email.setSmtpPort(465);
            email.setAuthentication("algozzinomatteo@gmail.com", "hgmt hctv pycy vfrr");
            email.setSSLOnConnect(true);

            
            email.setFrom("sistema@ortogest.it");
            email.setSubject(oggetto);
            email.setMsg(corpo);
            email.addTo(destinatario);

            
            
            
            
            
            
            
            email.send();

            LOGGER.info("[Adapter] L'Adapter ha configurato e instradato con successo la richiesta verso la libreria Apache Commons (Adaptee).");
            return true;
        } catch (EmailException e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'invio dell'email", e);
            return false;
        }
    }
}
