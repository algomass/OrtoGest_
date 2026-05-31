package it.ortogest.ortogestapp.dao;

import it.ortogest.ortogestapp.model.Lotto;
import it.ortogest.ortogestapp.model.Prodotto;
import it.ortogest.ortogestapp.utils.DatabaseHelper;
import it.ortogest.ortogestapp.utils.Printer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LottoDAOJdbc implements ILottoDAO {

    @Override
    public void salvaLotto(Lotto lotto) {
        String sql = "INSERT INTO lotto (id_lotto, nome_fornitore, nome_prodotto, quantita_kg, data_arrivo, data_scadenza, costo_acquisto, prezzo_vendita, sconto_attivo, prezzo_scontato) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE " +
                     "nome_fornitore = VALUES(nome_fornitore), " +
                     "nome_prodotto = VALUES(nome_prodotto), " +
                     "quantita_kg = VALUES(quantita_kg), " +
                     "data_arrivo = VALUES(data_arrivo), " +
                     "data_scadenza = VALUES(data_scadenza), " +
                     "costo_acquisto = VALUES(costo_acquisto), " +
                     "prezzo_vendita = VALUES(prezzo_vendita), " +
                     "sconto_attivo = VALUES(sconto_attivo), " +
                     "prezzo_scontato = VALUES(prezzo_scontato)";
                     
        try (Connection conn = DatabaseHelper.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, lotto.getIdLotto());
            stmt.setString(2, lotto.getNomeFornitore());
            stmt.setString(3, lotto.getTipologiaProdotto().getNome());
            stmt.setDouble(4, lotto.getQuantitaKg());
            stmt.setString(5, lotto.getDataArrivo().toString());
            stmt.setString(6, lotto.getDataScadenza().toString());
            stmt.setDouble(7, lotto.getCostoAcquisto());
            stmt.setDouble(8, lotto.getPrezzoVendita());
            stmt.setBoolean(9, lotto.isScontoScadenzaAttivo());
            stmt.setDouble(10, lotto.getPrezzoScontato());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            Printer.perror("Errore in salvaLotto: " + e.getMessage());
        }
    }

    @Override
    public List<Lotto> getTuttiILotti() {
        List<Lotto> lotti = new ArrayList<>();
        String sql = "SELECT id_lotto, nome_fornitore, nome_prodotto, quantita_kg, data_arrivo, data_scadenza, costo_acquisto, prezzo_vendita, sconto_attivo, prezzo_scontato FROM lotto";
        
        try (Connection conn = DatabaseHelper.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
             
            while (rs.next()) {
                lotti.add(estraiLotto(rs));
            }
        } catch (SQLException e) {
            Printer.perror("Errore in getTuttiILotti: " + e.getMessage());
        }
        return lotti;
    }

    @Override
    public Lotto trovaPerId(String idLotto) {
        String sql = "SELECT id_lotto, nome_fornitore, nome_prodotto, quantita_kg, data_arrivo, data_scadenza, costo_acquisto, prezzo_vendita, sconto_attivo, prezzo_scontato FROM lotto WHERE id_lotto = ?";
        try (Connection conn = DatabaseHelper.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, idLotto);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return estraiLotto(rs);
                }
            }
        } catch (SQLException e) {
            Printer.perror("Errore in trovaPerId: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Lotto> trovaPerProdotto(String nomeProdotto) {
        List<Lotto> lotti = new ArrayList<>();
        String sql = "SELECT id_lotto, nome_fornitore, nome_prodotto, quantita_kg, data_arrivo, data_scadenza, costo_acquisto, prezzo_vendita, sconto_attivo, prezzo_scontato FROM lotto WHERE nome_prodotto = ?";
        
        try (Connection conn = DatabaseHelper.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, nomeProdotto);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lotti.add(estraiLotto(rs));
                }
            }
        } catch (SQLException e) {
            Printer.perror("Errore in trovaPerProdotto: " + e.getMessage());
        }
        return lotti;
    }
    
    private Lotto estraiLotto(ResultSet rs) throws SQLException {
        String nomeProdotto = rs.getString("nome_prodotto");
        Prodotto prodotto = DAOFactory.getInstance().getProdottoDAO().trovaPerNome(nomeProdotto);
        
        return new Lotto(
            rs.getString("id_lotto"),
            rs.getString("nome_fornitore"),
            prodotto,
            rs.getDouble("quantita_kg"),
            LocalDate.parse(rs.getString("data_arrivo")),
            LocalDate.parse(rs.getString("data_scadenza")),
            rs.getDouble("costo_acquisto"),
            rs.getDouble("prezzo_vendita"),
            rs.getBoolean("sconto_attivo"),
            rs.getDouble("prezzo_scontato")
        );
    }

    @Override
    public void eliminaLotto(String idLotto) {
        String sql = "DELETE FROM lotto WHERE id_lotto = ?";
        try (Connection conn = DatabaseHelper.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, idLotto);
            stmt.executeUpdate();
        } catch (SQLException e) {
            Printer.perror("Errore in eliminaLotto: " + e.getMessage());
        }
    }

    @Override
    public double getPrezzoMedioAcquisto(String nomeProdotto) {
        String sql = "SELECT AVG(costo_acquisto) AS media FROM lotto WHERE nome_prodotto = ?";
        try (Connection conn = DatabaseHelper.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, nomeProdotto);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("media");
                }
            }
        } catch (SQLException e) {
            Printer.perror("Errore in getPrezzoMedioAcquisto: " + e.getMessage());
        }
        return 0.0;
    }
}
