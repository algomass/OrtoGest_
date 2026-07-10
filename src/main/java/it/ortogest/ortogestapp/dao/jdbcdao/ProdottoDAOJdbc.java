package it.ortogest.ortogestapp.dao.jdbcdao;

import it.ortogest.ortogestapp.dao.interfacedao.IProdottoDAO;
import it.ortogest.ortogestapp.model.Prodotto;
import it.ortogest.ortogestapp.utils.DatabaseHelper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ProdottoDAOJdbc implements IProdottoDAO {

    @Override
    public List<Prodotto> getTuttiIProdotti() {
        String sql = "SELECT nome, prezzo_attuale, quantita_disponibile, categoria FROM prodotto";
        return DatabaseHelper.getInstance().queryForList(sql, this::estraiProdotto, "Errore in getTuttiIProdotti");
    }

    @Override
    public void salvaProdotto(Prodotto prodotto) {
        String sql = "INSERT INTO prodotto (nome, prezzo_attuale, quantita_disponibile, categoria) " +
                "VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "prezzo_attuale = VALUES(prezzo_attuale), " +
                "quantita_disponibile = VALUES(quantita_disponibile), " +
                "categoria = VALUES(categoria)";

        DatabaseHelper.getInstance().executeUpdate(sql, "Errore in salvaProdotto",
                prodotto.getNome(), prodotto.getPrezzoAttuale(), prodotto.getQuantitaTotaleDisponibile(),
                prodotto.getCategoria());
    }

    @Override
    public Prodotto trovaPerNome(String nome) {
        String sql = "SELECT nome, prezzo_attuale, quantita_disponibile, categoria FROM prodotto WHERE nome = ?";
        return DatabaseHelper.getInstance().queryForObject(sql, this::estraiProdotto, "Errore in trovaPerNome", nome);
    }

    @Override
    public List<Prodotto> trovaPerCategoria(String categoria) {
        String sql = "SELECT nome, prezzo_attuale, quantita_disponibile, categoria FROM prodotto WHERE categoria = ?";
        return DatabaseHelper.getInstance().queryForList(sql, this::estraiProdotto, "Errore in trovaPerCategoria",
                categoria);
    }

    private Prodotto estraiProdotto(ResultSet rs) throws SQLException {
        return new Prodotto(
                rs.getString("nome"),
                rs.getDouble("prezzo_attuale"),
                rs.getDouble("quantita_disponibile"),
                rs.getString("categoria"));
    }

    @Override
    public void eliminaProdotto(String nome) {
        String sql = "DELETE FROM prodotto WHERE nome = ?";
        DatabaseHelper.getInstance().executeUpdate(sql, "Errore in eliminaProdotto", nome);
    }
}
