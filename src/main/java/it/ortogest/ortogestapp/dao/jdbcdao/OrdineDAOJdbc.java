package it.ortogest.ortogestapp.dao.jdbcdao;

import it.ortogest.ortogestapp.dao.interfacedao.IOrdineDAO;
import it.ortogest.ortogestapp.model.Ordine;
import it.ortogest.ortogestapp.model.RigaOrdine;
import it.ortogest.ortogestapp.utils.DatabaseHelper;
import it.ortogest.ortogestapp.utils.Printer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrdineDAOJdbc implements IOrdineDAO {

    private static final String COLUMN_STATO = "stato";

    @Override
    public void salvaOrdine(Ordine ordine) {
        String sqlOrdine = "INSERT INTO ordine (id_ordine, email_cliente, stato) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE stato = VALUES(stato)";

        String sqlRiga = "INSERT INTO riga_ordine (id_ordine, id_lotto, nome_prodotto, quantita, prezzo_fissato) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseHelper.getInstance().getConnection()) {
            conn.setAutoCommit(false); // Transazione per sicurezza

            try (PreparedStatement stmtOrdine = conn.prepareStatement(sqlOrdine)) {
                stmtOrdine.setString(1, ordine.getIdOrdine());
                stmtOrdine.setString(2, ordine.getEmailCliente());
                stmtOrdine.setString(3, ordine.getStato());
                stmtOrdine.executeUpdate();
            }

            // Per semplicità e pulizia in caso di aggiornamento, cancelliamo le righe
            // vecchie se esistono e le reinseriamo
            try (PreparedStatement stmtDelete = conn.prepareStatement("DELETE FROM riga_ordine WHERE id_ordine = ?")) {
                stmtDelete.setString(1, ordine.getIdOrdine());
                stmtDelete.executeUpdate();
            }

            try (PreparedStatement stmtRiga = conn.prepareStatement(sqlRiga)) {
                for (RigaOrdine riga : ordine.getRighe()) {
                    stmtRiga.setString(1, ordine.getIdOrdine());
                    stmtRiga.setString(2, riga.getIdLotto());
                    stmtRiga.setString(3, riga.getNomeProdotto());
                    stmtRiga.setDouble(4, riga.getQuantita());
                    stmtRiga.setDouble(5, riga.getPrezzoUnitario());
                    stmtRiga.addBatch();
                }
                stmtRiga.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            Printer.perror("Errore in salvaOrdine: " + e.getMessage());
        }
    }

    @Override
    public List<Ordine> trovaOrdiniCliente(String emailCliente) {
        List<Ordine> ordini = new ArrayList<>();
        String sql = "SELECT id_ordine, stato FROM ordine WHERE email_cliente = ?";

        try (Connection conn = DatabaseHelper.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, emailCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String idOrdine = rs.getString("id_ordine");
                    String stato = rs.getString(COLUMN_STATO);
                    List<RigaOrdine> righe = caricaRigheOrdine(conn, idOrdine);
                    ordini.add(new Ordine(idOrdine, emailCliente, righe, stato));
                }
            }
        } catch (SQLException e) {
            Printer.perror("Errore in trovaOrdiniCliente: " + e.getMessage());
        }
        return ordini;
    }

    private List<RigaOrdine> caricaRigheOrdine(Connection conn, String idOrdine) throws SQLException {
        List<RigaOrdine> righe = new ArrayList<>();
        String sql = "SELECT id_lotto, nome_prodotto, quantita, prezzo_fissato FROM riga_ordine WHERE id_ordine = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idOrdine);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    righe.add(new RigaOrdine(
                            rs.getString("nome_prodotto"),
                            rs.getString("id_lotto"),
                            rs.getDouble("quantita"),
                            rs.getDouble("prezzo_fissato")));
                }
            }
        }
        return righe;
    }

    @Override
    public Ordine trovaOrdinePerId(String idOrdine) {
        String sql = "SELECT email_cliente, stato FROM ordine WHERE id_ordine = ?";
        try (Connection conn = DatabaseHelper.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idOrdine);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String emailCliente = rs.getString("email_cliente");
                    String stato = rs.getString(COLUMN_STATO);
                    List<RigaOrdine> righe = caricaRigheOrdine(conn, idOrdine);
                    return new Ordine(idOrdine, emailCliente, righe, stato);
                }
            }
        } catch (SQLException e) {
            Printer.perror("Errore in trovaOrdinePerId: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void eliminaOrdine(String idOrdine) {
        String sqlRiga = "DELETE FROM riga_ordine WHERE id_ordine = ?";
        String sqlOrdine = "DELETE FROM ordine WHERE id_ordine = ?";

        try (Connection conn = DatabaseHelper.getInstance().getConnection()) {
            conn.setAutoCommit(false); // Transazione

            try (PreparedStatement stmtRiga = conn.prepareStatement(sqlRiga)) {
                stmtRiga.setString(1, idOrdine);
                stmtRiga.executeUpdate();
            }

            try (PreparedStatement stmtOrdine = conn.prepareStatement(sqlOrdine)) {
                stmtOrdine.setString(1, idOrdine);
                stmtOrdine.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            Printer.perror("Errore in eliminaOrdine: " + e.getMessage());
        }
    }

    @Override
    public List<Ordine> trovaTuttiOrdini() {
        List<Ordine> ordini = new ArrayList<>();
        String sql = "SELECT id_ordine, email_cliente, stato FROM ordine";

        try (Connection conn = DatabaseHelper.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String idOrdine = rs.getString("id_ordine");
                String emailCliente = rs.getString("email_cliente");
                String stato = rs.getString(COLUMN_STATO);
                List<RigaOrdine> righe = caricaRigheOrdine(conn, idOrdine);
                ordini.add(new Ordine(idOrdine, emailCliente, righe, stato));
            }
        } catch (SQLException e) {
            Printer.perror("Errore in trovaTuttiOrdini: " + e.getMessage());
        }
        return ordini;
    }

    @Override
    public void aggiornaStatoOrdine(String idOrdine, String nuovoStato) {
        String sql = "UPDATE ordine SET stato = ? WHERE id_ordine = ?";
        try (Connection conn = DatabaseHelper.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuovoStato);
            stmt.setString(2, idOrdine);
            stmt.executeUpdate();

        } catch (SQLException e) {
            Printer.perror("Errore in aggiornaStatoOrdine: " + e.getMessage());
        }
    }
}
