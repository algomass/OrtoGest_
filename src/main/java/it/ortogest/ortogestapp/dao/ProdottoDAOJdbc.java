package it.ortogest.ortogestapp.dao;

import it.ortogest.ortogestapp.model.Prodotto;
import it.ortogest.ortogestapp.utils.DatabaseHelper;
import it.ortogest.ortogestapp.utils.Printer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProdottoDAOJdbc implements IProdottoDAO {

    @Override
    public List<Prodotto> getTuttiIProdotti() {
        List<Prodotto> prodotti = new ArrayList<>();
        String sql = "SELECT * FROM prodotto";
        
        try (Connection conn = DatabaseHelper.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
             
            while (rs.next()) {
                prodotti.add(estraiProdotto(rs));
            }
        } catch (SQLException e) {
            Printer.perror("Errore in getTuttiIProdotti: " + e.getMessage());
        }
        return prodotti;
    }

    @Override
    public void salvaProdotto(Prodotto prodotto) {
        String sql = "INSERT INTO prodotto (nome, prezzo_attuale, quantita_disponibile, categoria, immagine_path) " +
                     "VALUES (?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE " +
                     "prezzo_attuale = VALUES(prezzo_attuale), " +
                     "quantita_disponibile = VALUES(quantita_disponibile), " +
                     "categoria = VALUES(categoria), " +
                     "immagine_path = VALUES(immagine_path)";
                     
        try (Connection conn = DatabaseHelper.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, prodotto.getNome());
            stmt.setDouble(2, prodotto.getPrezzoAttuale());
            stmt.setDouble(3, prodotto.getQuantitaTotaleDisponibile());
            stmt.setString(4, prodotto.getCategoria());
            stmt.setString(5, prodotto.getImmaginePath());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            Printer.perror("Errore in salvaProdotto: " + e.getMessage());
        }
    }

    @Override
    public Prodotto trovaPerNome(String nome) {
        String sql = "SELECT * FROM prodotto WHERE nome = ?";
        try (Connection conn = DatabaseHelper.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, nome);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return estraiProdotto(rs);
                }
            }
        } catch (SQLException e) {
            Printer.perror("Errore in trovaPerNome: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Prodotto> trovaPerCategoria(String categoria) {
        List<Prodotto> prodotti = new ArrayList<>();
        String sql = "SELECT * FROM prodotto WHERE categoria = ?";
        
        try (Connection conn = DatabaseHelper.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, categoria);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    prodotti.add(estraiProdotto(rs));
                }
            }
        } catch (SQLException e) {
            Printer.perror("Errore in trovaPerCategoria: " + e.getMessage());
        }
        return prodotti;
    }
    
    private Prodotto estraiProdotto(ResultSet rs) throws SQLException {
        return new Prodotto(
            rs.getString("nome"),
            rs.getDouble("prezzo_attuale"),
            rs.getDouble("quantita_disponibile"),
            rs.getString("categoria"),
            rs.getString("immagine_path")
        );
    }
}
