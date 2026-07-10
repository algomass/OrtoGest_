package it.ortogest.ortogestapp.dao.interfacedao;

import it.ortogest.ortogestapp.model.Utente;

public interface IUtenteDAO {
    Utente verificaCredenziali(String email, String password);
    boolean registraUtente(Utente utente);
}
