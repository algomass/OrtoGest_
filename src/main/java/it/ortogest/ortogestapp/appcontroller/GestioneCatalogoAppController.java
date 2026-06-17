package it.ortogest.ortogestapp.appcontroller;

import it.ortogest.ortogestapp.beans.ProdottoBean;
import it.ortogest.ortogestapp.dao.interfacedao.ILottoDAO;
import it.ortogest.ortogestapp.dao.interfacedao.IProdottoDAO;
import it.ortogest.ortogestapp.exception.GestioneException;
import it.ortogest.ortogestapp.model.Prodotto;
import it.ortogest.ortogestapp.pattern.abstractfactory.DAOFactory;
import it.ortogest.ortogestapp.beans.LottoBean;
import it.ortogest.ortogestapp.model.Lotto;

import java.util.ArrayList;
import java.util.List;

/**
 * Application Controller per la gestione del Catalogo Prodotti (lato
 * Responsabile).
 */
public class GestioneCatalogoAppController {

    /**
     * Recupera tutto il catalogo per la visualizzazione nella dashboard del
     * Responsabile.
     * 
     * @return Lista di ProdottoBean
     */
    public List<ProdottoBean> getTuttiIProdotti() {
        IProdottoDAO prodottoDAO = DAOFactory.getInstance().getProdottoDAO();
        ILottoDAO lottoDAO = DAOFactory.getInstance().getLottoDAO();
        List<Prodotto> prodotti = prodottoDAO.getTuttiIProdotti();
        List<ProdottoBean> beans = new ArrayList<>();

        for (Prodotto p : prodotti) {
            ProdottoBean bean = new ProdottoBean(
                    p.getNome(),
                    p.getPrezzoAttuale(),
                    p.getQuantitaTotaleDisponibile(),
                    p.getCategoria(),
                    p.getImmaginePath());
            bean.setPrezzoAcquistoMedio(lottoDAO.getPrezzoMedioAcquisto(p.getNome()));
            beans.add(bean);
        }

        return beans;
    }

    /**
     * Aggiorna solo la categoria di un prodotto.
     */
    public ProdottoBean aggiornaCategoriaProdotto(ProdottoBean bean) throws GestioneException {
        IProdottoDAO prodottoDAO = DAOFactory.getInstance().getProdottoDAO();
        Prodotto p = prodottoDAO.trovaPerNome(bean.getNome());

        if (p == null) {
            throw new GestioneException("Prodotto non trovato nel catalogo.");
        }

        if (bean.getCategoria() != null && !bean.getCategoria().isEmpty()) {
            p.setCategoria(bean.getCategoria());
        }

        prodottoDAO.salvaProdotto(p);
        return bean;
    }

    /**
     * Recupera i lotti associati a un prodotto.
     */
    public List<LottoBean> getLottiPerProdotto(String nomeProdotto) {
        ILottoDAO lottoDAO = DAOFactory.getInstance().getLottoDAO();
        List<Lotto> lotti = lottoDAO.trovaPerProdotto(nomeProdotto);
        List<LottoBean> beans = new ArrayList<>();

        for (Lotto l : lotti) {
            beans.add(LottoBean.builder()
                    .idLotto(l.getIdLotto())
                    .nomeFornitore(l.getNomeFornitore())
                    .nomeProdotto(l.getTipologiaProdotto().getNome())
                    .quantitaKg(l.getQuantitaKg())
                    .dataArrivo(l.getDataArrivo())
                    .dataScadenza(l.getDataScadenza())
                    .costoAcquisto(l.getCostoAcquisto())
                    .prezzoVendita(l.getPrezzoVendita())
                    .scontoScadenzaAttivo(l.isScontoScadenzaAttivo())
                    .prezzoScontato(l.getPrezzoScontato())
                    .build());
        }
        return beans;
    }

    /**
     * Aggiorna i dati di vendita di un lotto (Prezzo, Sconto).
     */
    public LottoBean aggiornaPrezzoLotto(LottoBean bean) throws GestioneException {
        if (bean.getPrezzoVendita() < 0) {
            throw new GestioneException("Il prezzo di vendita non può essere negativo.");
        }
        if (bean.isScontoScadenzaAttivo() && bean.getPrezzoScontato() < 0) {
            throw new GestioneException("Il prezzo scontato non può essere negativo.");
        }

        ILottoDAO lottoDAO = DAOFactory.getInstance().getLottoDAO();
        Lotto lotto = lottoDAO.trovaPerId(bean.getIdLotto());

        if (lotto == null) {
            throw new GestioneException("Lotto non trovato.");
        }

        lotto.setPrezzoVendita(bean.getPrezzoVendita());
        lotto.setScontoScadenzaAttivo(bean.isScontoScadenzaAttivo());
        lotto.setPrezzoScontato(bean.getPrezzoScontato());

        lottoDAO.salvaLotto(lotto);

        return bean;
    }

    /**
     * Recupera i lotti in scadenza entro il numero di giorni indicato, non ancora
     * scontati.
     */
    public List<LottoBean> getLottiInScadenza(int giorniPreavviso) {
        ILottoDAO lottoDAO = DAOFactory.getInstance().getLottoDAO();
        List<Lotto> tuttiLotti = lottoDAO.getTuttiILotti();
        List<LottoBean> beans = new ArrayList<>();
        java.time.LocalDate limite = java.time.LocalDate.now(java.time.ZoneId.systemDefault())
                .plusDays(giorniPreavviso);

        for (Lotto l : tuttiLotti) {
            // Mostra i lotti se la loro scadenza è <= al limite e non sono già stati messi
            // in sconto
            if (!l.isScontoScadenzaAttivo() && !l.getDataScadenza().isAfter(limite)) {
                beans.add(LottoBean.builder()
                        .idLotto(l.getIdLotto())
                        .nomeFornitore(l.getNomeFornitore())
                        .nomeProdotto(l.getTipologiaProdotto().getNome())
                        .quantitaKg(l.getQuantitaKg())
                        .dataArrivo(l.getDataArrivo())
                        .dataScadenza(l.getDataScadenza())
                        .costoAcquisto(l.getCostoAcquisto())
                        .prezzoVendita(l.getPrezzoVendita())
                        .scontoScadenzaAttivo(l.isScontoScadenzaAttivo())
                        .prezzoScontato(l.getPrezzoScontato())
                        .build());
            }
        }
        return beans;
    }
}
