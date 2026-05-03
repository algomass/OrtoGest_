package it.ortogest.ortogestapp.dao;

import it.ortogest.ortogestapp.model.Utente;

public interface IUtenteDAO {
    Utente verificaCredenziali(String email, String password);
}
