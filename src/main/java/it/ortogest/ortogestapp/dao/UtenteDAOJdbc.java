package it.ortogest.ortogestapp.dao;

import it.ortogest.ortogestapp.model.Utente;
import it.ortogest.ortogestapp.utils.DatabaseHelper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UtenteDAOJdbc implements IUtenteDAO {

    @Override
    public Utente verificaCredenziali(String email, String password) {
        String sql = "SELECT email, password, ruolo FROM utente WHERE email = ? AND password = ?";
        return DatabaseHelper.getInstance().queryForObject(sql, rs -> {
            String nome = getNomeOrDefault(rs, "Utente");
            return new Utente(nome, rs.getString("email"), rs.getString("password"), rs.getString("ruolo"));
        }, "Errore in verificaCredenziali", email, password);
    }

    private String getNomeOrDefault(ResultSet rs, String fallback) {
        try {
            return rs.getString("nome");
        } catch (SQLException _) {
            return fallback;
        }
    }
}
