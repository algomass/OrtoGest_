package it.ortogest.ortogestapp.dao;

import it.ortogest.ortogestapp.model.Utente;
import it.ortogest.ortogestapp.utils.DatabaseHelper;
import it.ortogest.ortogestapp.utils.Printer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UtenteDAOJdbc implements IUtenteDAO {

    @Override
    public Utente verificaCredenziali(String email, String password) {
        String sql = "SELECT email, password, ruolo FROM utente WHERE email = ? AND password = ?";
        try (Connection conn = DatabaseHelper.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, email);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String nome = getNomeOrDefault(rs, "Utente");
                    
                    return new Utente(nome, rs.getString("email"), rs.getString("password"), rs.getString("ruolo"));
                }
            }
        } catch (SQLException e) {
            Printer.perror("Errore in verificaCredenziali: " + e.getMessage());
        }
        return null;
    }

    private String getNomeOrDefault(ResultSet rs, String fallback) {
        try {
            return rs.getString("nome");
        } catch (SQLException _) {
            return fallback;
        }
    }
}
