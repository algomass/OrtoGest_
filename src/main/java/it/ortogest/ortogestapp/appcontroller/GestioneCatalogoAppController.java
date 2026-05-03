package it.ortogest.ortogestapp.appcontroller;

import it.ortogest.ortogestapp.beans.ProdottoBean;
import it.ortogest.ortogestapp.dao.DAOFactory;
import it.ortogest.ortogestapp.dao.IProdottoDAO;
import it.ortogest.ortogestapp.exception.GestioneException;
import it.ortogest.ortogestapp.model.Prodotto;

import java.util.ArrayList;
import java.util.List;

/**
 * Application Controller per la gestione del Catalogo Prodotti (lato Responsabile).
 */
public class GestioneCatalogoAppController {

    /**
     * Recupera tutto il catalogo per la visualizzazione nella dashboard del Responsabile.
     * @return Lista di ProdottoBean
     */
    public List<ProdottoBean> getTuttiIProdotti() {
        IProdottoDAO prodottoDAO = DAOFactory.getInstance().getProdottoDAO();
        List<Prodotto> prodotti = prodottoDAO.getTuttiIProdotti();
        List<ProdottoBean> beans = new ArrayList<>();

        for (Prodotto p : prodotti) {
            beans.add(new ProdottoBean(
                    p.getNome(),
                    p.getPrezzoAttuale(),
                    p.getQuantitaTotaleDisponibile(),
                    p.getCategoria(),
                    p.getImmaginePath()
            ));
        }

        return beans;
    }

    /**
     * Aggiorna il prezzo al pubblico di un prodotto.
     * @param bean Bean contenente il nome del prodotto e il nuovo prezzo.
     * @return Bean aggiornato se il salvataggio va a buon fine.
     * @throws GestioneException se il prodotto non viene trovato o il prezzo è non valido.
     */
    public ProdottoBean aggiornaPrezzoProdotto(ProdottoBean bean) throws GestioneException {
        if (bean.getPrezzoAttuale() < 0) {
            throw new GestioneException("Il prezzo non può essere negativo.");
        }

        IProdottoDAO prodottoDAO = DAOFactory.getInstance().getProdottoDAO();
        Prodotto p = prodottoDAO.trovaPerNome(bean.getNome());

        if (p == null) {
            throw new GestioneException("Prodotto non trovato nel catalogo.");
        }

        // Aggiorniamo il prezzo e la categoria nel dominio
        p.setPrezzoAttuale(bean.getPrezzoAttuale());
        if (bean.getCategoria() != null && !bean.getCategoria().isEmpty()) {
            p.setCategoria(bean.getCategoria());
        }

        // Nel caso di un DB reale, qui chiameremmo prodottoDAO.aggiorna(p);
        // Poiché usiamo un Mock in memoria basato su reference, la modifica si propaga automaticamente.

        return bean;
    }
}
