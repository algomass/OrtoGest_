package it.ortogest.ortogestapp.appcontroller;

import it.ortogest.ortogestapp.beans.CredenzialiBean;
import it.ortogest.ortogestapp.beans.UtenteBean;
import it.ortogest.ortogestapp.dao.interfacedao.IUtenteDAO;
import it.ortogest.ortogestapp.exception.LoginFallitoException;
import it.ortogest.ortogestapp.model.Utente;
import it.ortogest.ortogestapp.pattern.abstractfactory.DAOFactory;


public class LoginAppController {

    public UtenteBean login(CredenzialiBean credenziali) throws LoginFallitoException {
        
        IUtenteDAO utenteDAO = DAOFactory.getInstance().getUtenteDAO();
        Utente utenteModel = utenteDAO.verificaCredenziali(credenziali.getEmail(), credenziali.getPassword());

        
        if (utenteModel == null) {
            
            throw new LoginFallitoException("Credenziali non valide o utente inesistente.");
        }

        
        if (!utenteModel.getEmail().equals(credenziali.getEmail()) || !utenteModel.getPassword().equals(credenziali.getPassword())) {
            throw new LoginFallitoException("Credenziali non valide o utente inesistente.");
        }

        

        
        
        return new UtenteBean(
                utenteModel.getNome(),
                utenteModel.getEmail(),
                utenteModel.getRuolo());
    }
}
