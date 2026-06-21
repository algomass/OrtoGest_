package it.ortogest.ortogestapp.appcontroller;

import it.ortogest.ortogestapp.beans.OrdineBean;
import it.ortogest.ortogestapp.dao.interfacedao.IOrdineDAO;
import it.ortogest.ortogestapp.exception.GestioneException;
import it.ortogest.ortogestapp.model.Ordine;
import it.ortogest.ortogestapp.pattern.abstractfactory.DAOFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Use Case Controller per la registrazione di una vendita in negozio (Operatore/Cassa).
 */
public class RegistraVenditaAppController {

    private IOrdineDAO ordineDAO;

    public RegistraVenditaAppController() {
        this.ordineDAO = DAOFactory.getInstance().getOrdineDAO();
    }

    /**
     * Recupera la lista degli ordini che sono stati preparati e attendono solo il pagamento in cassa.
     */
    public List<OrdineBean> getOrdiniProntiPerRitiro() {
        List<Ordine> tuttiOrdini = ordineDAO.trovaTuttiOrdini();
        List<OrdineBean> pronti = new ArrayList<>();

        for (Ordine o : tuttiOrdini) {
            if ("Pronto per il Ritiro".equals(o.getStato())) {
                pronti.add(new OrdineBean(
                        o.getIdOrdine(),
                        o.getRiepilogoProdotti(),
                        o.getTotale(),
                        o.getStato(),
                        o.getEmailCliente()));
            }
        }
        return pronti;
    }

    /**
     * Finalizza la vendita dell'ordine, registrando il pagamento e l'uscita fisica della merce.
     */
    public void registraVendita(String idOrdine) throws GestioneException {
        Ordine ordine = ordineDAO.trovaOrdinePerId(idOrdine);
        if (ordine == null) {
            throw new GestioneException("Ordine non trovato.");
        }
        if (!"Pronto per il Ritiro".equals(ordine.getStato())) {
            throw new GestioneException("L'ordine non è pronto per il ritiro.");
        }

        ordineDAO.aggiornaStatoOrdine(idOrdine, "Ritirato");
    }
}
