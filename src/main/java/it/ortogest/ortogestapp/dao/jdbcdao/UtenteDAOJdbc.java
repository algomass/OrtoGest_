package it.ortogest.ortogestapp.dao.jdbcdao;

import it.ortogest.ortogestapp.dao.interfacedao.IUtenteDAO;
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

    @Override
    public boolean registraUtente(Utente utente) {
        String sql = "INSERT INTO utente (nome, email, password, ruolo) VALUES (?, ?, ?, ?)";
        try {
            DatabaseHelper.getInstance().executeUpdate(sql, "Errore durante la registrazione", 
                utente.getNome(), utente.getEmail(), utente.getPassword(), utente.getRuolo());
            return true;
        } catch (Exception e) {
            it.ortogest.ortogestapp.utils.Printer.perror("Errore registrazione: " + e.getMessage());
            return false;
        }
    }
}
