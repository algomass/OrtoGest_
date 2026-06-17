package it.ortogest.ortogestapp.appcontroller;

import it.ortogest.ortogestapp.beans.CredenzialiBean;
import it.ortogest.ortogestapp.beans.UtenteBean;
import it.ortogest.ortogestapp.dao.InterfaceDAO.IUtenteDAO;
import it.ortogest.ortogestapp.exception.LoginFallitoException;
import it.ortogest.ortogestapp.model.Utente;
import it.ortogest.ortogestapp.pattern.AbstractFactory.DAOFactory;

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

        // Qui in futuro potresti aggiungere altre regole.

        // 3. Rispetto la regola "Isolamento Tramite Beans":
        // Non restituisco mai l'Entity "Utente" alla vista, ma creo un "UtenteBean"
        return new UtenteBean(
                utenteModel.getNome(),
                utenteModel.getEmail(),
                utenteModel.getRuolo());
    }
}
