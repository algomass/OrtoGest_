package it.ortogest.ortogestapp.dao;

import it.ortogest.ortogestapp.model.Utente;

/**
 * Data Access Object per l'entità Utente.
 * Si occupa ESCLUSIVAMENTE di parlare col database (o di simularlo, come in
 * questo caso).
 */
public class UtenteDAO {

    public Utente verificaCredenziali(String email, String password) {
        // SIMULAZIONE DATABASE:
        // In futuro qui ci sarà il codice JDBC (es. SELECT * FROM utenti WHERE
        // email=...)
        if ("a".equals(email) && "a".equals(password)) {
            // Se le trova, restituisce l'Entity popolata dal DB
            return new Utente("Mario Rossi", email, password, "Responsabile");
        }

        // Se non trova corrispondenze, restituisce null
        return null;
    }
}
