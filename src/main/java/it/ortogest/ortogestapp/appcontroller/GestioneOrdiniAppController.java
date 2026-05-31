package it.ortogest.ortogestapp.appcontroller;

import it.ortogest.ortogestapp.beans.OrdineBean;
import it.ortogest.ortogestapp.beans.ProdottoBean;
import it.ortogest.ortogestapp.dao.DAOFactory;
import it.ortogest.ortogestapp.dao.IOrdineDAO;
import it.ortogest.ortogestapp.dao.IProdottoDAO;
import it.ortogest.ortogestapp.exception.GestioneException;
import it.ortogest.ortogestapp.model.Ordine;
import it.ortogest.ortogestapp.model.Prodotto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Application Controller per la gestione degli Ordini (lato Cliente).
 */
public class GestioneOrdiniAppController {

    private IProdottoDAO prodottoDAO;
    private IOrdineDAO ordineDAO;

    public GestioneOrdiniAppController() {
        this.prodottoDAO = DAOFactory.getInstance().getProdottoDAO();
        this.ordineDAO = DAOFactory.getInstance().getOrdineDAO();
    }

    public List<ProdottoBean> getCatalogoDisponibile() {
        List<Prodotto> prodotti = prodottoDAO.getTuttiIProdotti();
        List<ProdottoBean> catalogoBeans = new ArrayList<>();
        
        for (Prodotto p : prodotti) {
            if (p.getQuantitaTotaleDisponibile() > 0 && p.getPrezzoAttuale() > 0) {
                catalogoBeans.add(new ProdottoBean(
                        p.getNome(),
                        p.getPrezzoAttuale(),
                        p.getQuantitaTotaleDisponibile(),
                        p.getCategoria(),
                        p.getImmaginePath()
                ));
            }
        }
        return catalogoBeans;
    }

    /**
     * Restituisce i prodotti disponibili filtrati per categoria.
     * @param categoria La categoria richiesta ("Frutta" o "Verdura").
     * @return Lista di ProdottoBean con giacenza > 0 e categoria corrispondente.
     */
    public List<ProdottoBean> getCatalogoPerCategoria(String categoria) {
        List<Prodotto> prodotti = prodottoDAO.trovaPerCategoria(categoria);
        List<ProdottoBean> catalogoBeans = new ArrayList<>();

        for (Prodotto p : prodotti) {
            if (p.getQuantitaTotaleDisponibile() > 0 && p.getPrezzoAttuale() > 0) {
                catalogoBeans.add(new ProdottoBean(
                        p.getNome(),
                        p.getPrezzoAttuale(),
                        p.getQuantitaTotaleDisponibile(),
                        p.getCategoria(),
                        p.getImmaginePath()
                ));
            }
        }
        return catalogoBeans;
    }

    /**
     * Crea un nuovo ordine a partire da una lista di prodotti nel carrello.
     * @param emailCliente L'email del cliente che effettua l'ordine.
     * @param carrello La lista di prodotti e quantità selezionate.
     * @return Messaggio di conferma con l'ID dell'ordine.
     * @throws GestioneException Se un prodotto non è trovato o la giacenza è insufficiente.
     */
    public String creaOrdine(String emailCliente, List<it.ortogest.ortogestapp.beans.RigaOrdineBean> carrello) throws GestioneException {
        if (carrello == null || carrello.isEmpty()) {
            throw new GestioneException("Il carrello è vuoto.");
        }

        List<it.ortogest.ortogestapp.model.RigaOrdine> righeModello = new ArrayList<>();

        // Validazione preliminare di tutti i prodotti e giacenze
        for (it.ortogest.ortogestapp.beans.RigaOrdineBean rigaBean : carrello) {
            Prodotto p = prodottoDAO.trovaPerNome(rigaBean.getNomeProdotto());
            if (p == null) {
                throw new GestioneException("Prodotto '" + rigaBean.getNomeProdotto() + "' non trovato.");
            }
            if (p.getQuantitaTotaleDisponibile() < rigaBean.getQuantita()) {
                throw new GestioneException("Giacenza insufficiente per: " + rigaBean.getNomeProdotto());
            }
            // Creiamo la riga per il modello di dominio
            righeModello.add(new it.ortogest.ortogestapp.model.RigaOrdine(
                    p.getNome(), 
                    rigaBean.getQuantita(), 
                    p.getPrezzoAttuale()
            ));
        }

        // Simula la creazione dell'ID univoco
        String idOrdine = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        // Creazione ordine
        Ordine nuovoOrdine = new Ordine(idOrdine, emailCliente, righeModello, "Inviato");
        
        // Salviamo nel DB
        ordineDAO.salvaOrdine(nuovoOrdine);
        
        // Scaliamo le giacenze per ogni prodotto nell'ordine
        for (it.ortogest.ortogestapp.model.RigaOrdine riga : righeModello) {
            Prodotto p = prodottoDAO.trovaPerNome(riga.getNomeProdotto());
            p.sottraiGiacenza(riga.getQuantita());
            prodottoDAO.salvaProdotto(p);
        }
        
        return "Ordine completato con ID: " + idOrdine;
    }

    public List<OrdineBean> getOrdiniCliente(String emailCliente) {
        List<Ordine> ordini = ordineDAO.trovaOrdiniCliente(emailCliente);
        List<OrdineBean> ordineBeans = new ArrayList<>();
        
        for (Ordine o : ordini) {
            ordineBeans.add(new OrdineBean(
                o.getIdOrdine(), 
                o.getRiepilogoProdotti(), 
                o.getTotale(), 
                o.getStato()
            ));
        }
        return ordineBeans;
    }

    public void eliminaOrdine(String idOrdine) throws GestioneException {
        Ordine ordine = ordineDAO.trovaOrdinePerId(idOrdine);
        if (ordine == null) {
            throw new GestioneException("Ordine non trovato.");
        }
        
        // Ripristina le giacenze dei prodotti
        for (it.ortogest.ortogestapp.model.RigaOrdine riga : ordine.getRighe()) {
            Prodotto p = prodottoDAO.trovaPerNome(riga.getNomeProdotto());
            if (p != null) {
                p.aggiungiGiacenza(riga.getQuantita());
                prodottoDAO.salvaProdotto(p);
            }
        }
        
        // Elimina l'ordine (e le righe associate) dal database
        ordineDAO.eliminaOrdine(idOrdine);
    }
}
