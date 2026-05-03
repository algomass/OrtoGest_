package it.ortogest.ortogestapp.appcontroller;

import it.ortogest.ortogestapp.beans.CredenzialiBean;
import it.ortogest.ortogestapp.beans.UtenteBean;
import it.ortogest.ortogestapp.dao.DAOFactory;
import it.ortogest.ortogestapp.dao.IUtenteDAO;
import it.ortogest.ortogestapp.exception.LoginFallitoException;
import it.ortogest.ortogestapp.model.Utente;

/**
 * Controller Applicativo (Il vero controller del pattern BCE).
 * Contiene la logica di business del caso d'uso "Login".
 */
public class LoginAppController {

    public UtenteBean login(CredenzialiBean credenziali) throws LoginFallitoException {
        // 1. Chiamo il DAO per verificare i dati nel sistema di persistenza
        IUtenteDAO utenteDAO = DAOFactory.getInstance().getUtenteDAO();
        Utente utenteModel = utenteDAO.verificaCredenziali(credenziali.getEmail(), credenziali.getPassword());
        
        // 2. Applico le regole di business
        if (utenteModel == null) {
            // Nessun utente trovato con quelle credenziali
            throw new LoginFallitoException("Credenziali non valide o utente inesistente.");
        }
        
        // Qui in futuro potresti aggiungere altre regole:
        // es. if (utenteModel.isBloccato()) throw new AccountBloccatoException()...
        
        // 3. Rispetto la regola "Isolamento Tramite Beans":
        // Non restituisco mai l'Entity "Utente" alla vista, ma creo un "UtenteBean"
        UtenteBean utenteLoggato = new UtenteBean(
            utenteModel.getNome(), 
            utenteModel.getEmail(), 
            utenteModel.getRuolo()
        );
        
        return utenteLoggato;
    }
}
