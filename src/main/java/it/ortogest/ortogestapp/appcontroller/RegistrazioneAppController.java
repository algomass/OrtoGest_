package it.ortogest.ortogestapp.appcontroller;

import it.ortogest.ortogestapp.beans.NuovoUtenteBean;
import it.ortogest.ortogestapp.dao.interfacedao.IUtenteDAO;
import it.ortogest.ortogestapp.model.Utente;
import it.ortogest.ortogestapp.pattern.abstractfactory.DAOFactory;

public class RegistrazioneAppController {

    public boolean registraNuovoCliente(NuovoUtenteBean nuovoUtenteBean) throws Exception {
        IUtenteDAO utenteDAO = DAOFactory.getInstance().getUtenteDAO();

        Utente nuovoUtente = new Utente(
                nuovoUtenteBean.getNome(),
                nuovoUtenteBean.getEmail(),
                nuovoUtenteBean.getPassword(),
                nuovoUtenteBean.getRuolo());

        return utenteDAO.registraUtente(nuovoUtente);
    }
}
