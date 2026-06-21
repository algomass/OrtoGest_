package it.ortogest.ortogestapp.appcontroller;

import it.ortogest.ortogestapp.beans.LottoBean;
import it.ortogest.ortogestapp.beans.ProdottoBean;
import it.ortogest.ortogestapp.dao.interfacedao.ILottoDAO;
import it.ortogest.ortogestapp.dao.interfacedao.IProdottoDAO;
import it.ortogest.ortogestapp.exception.GestioneException;
import it.ortogest.ortogestapp.model.Lotto;
import it.ortogest.ortogestapp.model.Prodotto;
import it.ortogest.ortogestapp.pattern.abstractfactory.DAOFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Use Case Controller per l'aggiunta di un lotto al catalogo (Responsabile).
 * Si occupa di prezzare i nuovi lotti e definirne le categorie.
 */
public class AggiungiLottoAppController {

    public List<ProdottoBean> getTuttiIProdotti() {
        return getTuttiIProdotti(false);
    }

    public List<ProdottoBean> getTuttiIProdotti(boolean includiStorico) {
        IProdottoDAO prodottoDAO = DAOFactory.getInstance().getProdottoDAO();
        ILottoDAO lottoDAO = DAOFactory.getInstance().getLottoDAO();
        List<Prodotto> prodotti = prodottoDAO.getTuttiIProdotti();
        List<ProdottoBean> beans = new ArrayList<>();

        for (Prodotto p : prodotti) {
            List<Lotto> lotti = lottoDAO.trovaPerProdotto(p.getNome());
            double validGiacenza = 0.0;
            boolean hasValidLots = false;

            for (Lotto l : lotti) {
                if (includiStorico || l.getStato() == it.ortogest.ortogestapp.model.StatoLotto.ATTIVO) {
                    hasValidLots = true;
                    validGiacenza += l.getQuantitaKg();
                }
            }

            if (includiStorico || (hasValidLots && validGiacenza > 0)) {
                ProdottoBean bean = new ProdottoBean(
                        p.getNome(),
                        p.getPrezzoAttuale(),
                        validGiacenza,
                        p.getCategoria(),
                        p.getImmaginePath());
                bean.setPrezzoAcquistoMedio(lottoDAO.getPrezzoMedioAcquisto(p.getNome()));
                beans.add(bean);
            }
        }

        return beans;
    }

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

    public List<LottoBean> getLottiDaPrezzare() {
        ILottoDAO lottoDAO = DAOFactory.getInstance().getLottoDAO();
        List<Lotto> tuttiLotti = lottoDAO.getTuttiILotti();
        List<LottoBean> beans = new ArrayList<>();

        for (Lotto l : tuttiLotti) {
            if (l.getStato() == it.ortogest.ortogestapp.model.StatoLotto.ATTIVO && l.getPrezzoVendita() == 0.0) {
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

    public LottoBean impostaPrezzoLotto(LottoBean bean) throws GestioneException {
        if (bean.getPrezzoVendita() < 0) {
            throw new GestioneException("Il prezzo di vendita non può essere negativo.");
        }

        ILottoDAO lottoDAO = DAOFactory.getInstance().getLottoDAO();
        Lotto lotto = lottoDAO.trovaPerId(bean.getIdLotto());

        if (lotto == null) {
            throw new GestioneException("Lotto non trovato.");
        }

        lotto.setPrezzoVendita(bean.getPrezzoVendita());
        lottoDAO.salvaLotto(lotto);

        return bean;
    }
}
