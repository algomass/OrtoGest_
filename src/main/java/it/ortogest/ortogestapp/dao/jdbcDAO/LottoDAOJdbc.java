package it.ortogest.ortogestapp.dao.jdbcdao;

import it.ortogest.ortogestapp.dao.interfacedao.ILottoDAO;
import it.ortogest.ortogestapp.model.Lotto;
import it.ortogest.ortogestapp.model.Prodotto;
import it.ortogest.ortogestapp.pattern.abstractfactory.DAOFactory;
import it.ortogest.ortogestapp.utils.DatabaseHelper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class LottoDAOJdbc implements ILottoDAO {

    @Override
    public void salvaLotto(Lotto lotto) {
        String sql = "INSERT INTO lotto (id_lotto, nome_fornitore, nome_prodotto, quantita_kg, data_arrivo, data_scadenza, costo_acquisto, prezzo_vendita, sconto_attivo, prezzo_scontato) "
                +
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

        DatabaseHelper.getInstance().executeUpdate(sql, "Errore in salvaLotto",
                lotto.getIdLotto(), lotto.getNomeFornitore(), lotto.getTipologiaProdotto().getNome(),
                lotto.getQuantitaKg(), lotto.getDataArrivo().toString(), lotto.getDataScadenza().toString(),
                lotto.getCostoAcquisto(), lotto.getPrezzoVendita(), lotto.isScontoScadenzaAttivo(),
                lotto.getPrezzoScontato());
    }

    @Override
    public List<Lotto> getTuttiILotti() {
        String sql = "SELECT id_lotto, nome_fornitore, nome_prodotto, quantita_kg, data_arrivo, data_scadenza, costo_acquisto, prezzo_vendita, sconto_attivo, prezzo_scontato FROM lotto";
        return DatabaseHelper.getInstance().queryForList(sql, this::estraiLotto, "Errore in getTuttiILotti");
    }

    @Override
    public Lotto trovaPerId(String idLotto) {
        String sql = "SELECT id_lotto, nome_fornitore, nome_prodotto, quantita_kg, data_arrivo, data_scadenza, costo_acquisto, prezzo_vendita, sconto_attivo, prezzo_scontato FROM lotto WHERE id_lotto = ?";
        return DatabaseHelper.getInstance().queryForObject(sql, this::estraiLotto, "Errore in trovaPerId", idLotto);
    }

    @Override
    public List<Lotto> trovaPerProdotto(String nomeProdotto) {
        String sql = "SELECT id_lotto, nome_fornitore, nome_prodotto, quantita_kg, data_arrivo, data_scadenza, costo_acquisto, prezzo_vendita, sconto_attivo, prezzo_scontato FROM lotto WHERE nome_prodotto = ?";
        return DatabaseHelper.getInstance().queryForList(sql, this::estraiLotto, "Errore in trovaPerProdotto",
                nomeProdotto);
    }

    private Lotto estraiLotto(ResultSet rs) throws SQLException {
        String nomeProdotto = rs.getString("nome_prodotto");
        Prodotto prodotto = DAOFactory.getInstance().getProdottoDAO().trovaPerNome(nomeProdotto);

        return Lotto.builder()
                .idLotto(rs.getString("id_lotto"))
                .nomeFornitore(rs.getString("nome_fornitore"))
                .tipologiaProdotto(prodotto)
                .quantitaKg(rs.getDouble("quantita_kg"))
                .dataArrivo(LocalDate.parse(rs.getString("data_arrivo")))
                .dataScadenza(LocalDate.parse(rs.getString("data_scadenza")))
                .costoAcquisto(rs.getDouble("costo_acquisto"))
                .prezzoVendita(rs.getDouble("prezzo_vendita"))
                .scontoScadenzaAttivo(rs.getBoolean("sconto_attivo"))
                .prezzoScontato(rs.getDouble("prezzo_scontato"))
                .build();
    }

    @Override
    public void eliminaLotto(String idLotto) {
        String sql = "DELETE FROM lotto WHERE id_lotto = ?";
        DatabaseHelper.getInstance().executeUpdate(sql, "Errore in eliminaLotto", idLotto);
    }

    @Override
    public double getPrezzoMedioAcquisto(String nomeProdotto) {
        String sql = "SELECT AVG(costo_acquisto) AS media FROM lotto WHERE nome_prodotto = ?";
        Double media = DatabaseHelper.getInstance().queryForObject(sql, rs -> rs.getDouble("media"),
                "Errore in getPrezzoMedioAcquisto", nomeProdotto);
        return media != null ? media : 0.0;
    }
}
