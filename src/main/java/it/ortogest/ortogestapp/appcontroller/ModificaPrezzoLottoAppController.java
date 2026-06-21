package it.ortogest.ortogestapp.appcontroller;

import it.ortogest.ortogestapp.beans.LottoBean;
import it.ortogest.ortogestapp.dao.interfacedao.ILottoDAO;
import it.ortogest.ortogestapp.exception.GestioneException;
import it.ortogest.ortogestapp.model.Lotto;
import it.ortogest.ortogestapp.pattern.abstractfactory.DAOFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Use Case Controller per la modifica dei prezzi e scontistica dei lotti a catalogo (Responsabile).
 */
public class ModificaPrezzoLottoAppController {

    public List<LottoBean> getLottiPerProdotto(String nomeProdotto) {
        return getLottiPerProdotto(nomeProdotto, false);
    }

    public List<LottoBean> getLottiPerProdotto(String nomeProdotto, boolean includiStorico) {
        ILottoDAO lottoDAO = DAOFactory.getInstance().getLottoDAO();
        List<Lotto> lotti = lottoDAO.trovaPerProdotto(nomeProdotto);
        List<LottoBean> beans = new ArrayList<>();

        for (Lotto l : lotti) {
            if (includiStorico || l.getStato() == it.ortogest.ortogestapp.model.StatoLotto.ATTIVO) {
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

    public List<LottoBean> getLottiInScadenza(int giorniPreavviso) {
        ILottoDAO lottoDAO = DAOFactory.getInstance().getLottoDAO();
        List<Lotto> tuttiLotti = lottoDAO.getTuttiILotti();
        List<LottoBean> beans = new ArrayList<>();
        java.time.LocalDate limite = java.time.LocalDate.now(java.time.ZoneId.systemDefault())
                .plusDays(giorniPreavviso);

        for (Lotto l : tuttiLotti) {
            if (l.getStato() == it.ortogest.ortogestapp.model.StatoLotto.ATTIVO && !l.isScontoScadenzaAttivo() && !l.getDataScadenza().isAfter(limite)) {
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
}
