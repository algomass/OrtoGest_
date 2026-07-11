package it.ortogest.ortogestapp.appcontroller;

import it.ortogest.ortogestapp.beans.NuovoUtenteBean;
import it.ortogest.ortogestapp.dao.interfacedao.IUtenteDAO;
import it.ortogest.ortogestapp.model.Utente;
import it.ortogest.ortogestapp.pattern.abstractfactory.DAOFactory;

public class RegistrazioneAppController {

    private static RegistrazioneAppController instance;

    private RegistrazioneAppController() {}

    public static RegistrazioneAppController getInstance() {
        if (instance == null) {
            instance = new RegistrazioneAppController();
        }
        return instance;
    }

    public boolean registraNuovoCliente(NuovoUtenteBean nuovoUtenteBean) {
        IUtenteDAO utenteDAO = DAOFactory.getInstance().getUtenteDAO();

        Utente nuovoUtente = new Utente(
                nuovoUtenteBean.getNome(),
                nuovoUtenteBean.getEmail(),
                nuovoUtenteBean.getPassword(),
                nuovoUtenteBean.getRuolo());

        return utenteDAO.registraUtente(nuovoUtente);
    }
}
