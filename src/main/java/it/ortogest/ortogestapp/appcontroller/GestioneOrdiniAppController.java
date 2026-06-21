package it.ortogest.ortogestapp.appcontroller;

import it.ortogest.ortogestapp.beans.OrdineBean;
import it.ortogest.ortogestapp.beans.ProdottoBean;
import it.ortogest.ortogestapp.dao.interfacedao.ILottoDAO;
import it.ortogest.ortogestapp.dao.interfacedao.IOrdineDAO;
import it.ortogest.ortogestapp.dao.interfacedao.IProdottoDAO;
import it.ortogest.ortogestapp.exception.GestioneException;
import it.ortogest.ortogestapp.model.Lotto;
import it.ortogest.ortogestapp.model.Ordine;
import it.ortogest.ortogestapp.model.Prodotto;
import it.ortogest.ortogestapp.pattern.abstractfactory.DAOFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Application Controller per la gestione degli Ordini (lato Cliente).
 */
public class GestioneOrdiniAppController {

    private IProdottoDAO prodottoDAO;
    private IOrdineDAO ordineDAO;
    private ILottoDAO lottoDAO;

    public GestioneOrdiniAppController() {
        this.prodottoDAO = DAOFactory.getInstance().getProdottoDAO();
        this.ordineDAO = DAOFactory.getInstance().getOrdineDAO();
        this.lottoDAO = DAOFactory.getInstance().getLottoDAO();
    }

    public List<ProdottoBean> getCatalogoDisponibile() {
        List<Prodotto> prodotti = prodottoDAO.getTuttiIProdotti();
        List<ProdottoBean> catalogoBeans = new ArrayList<>();

        for (Prodotto p : prodotti) {
            ProdottoBean bean = elaboraProdottoRiepilogo(p);
            if (bean != null) {
                catalogoBeans.add(bean);
            }
        }
        return catalogoBeans;
    }

    public List<ProdottoBean> getCatalogoPerCategoria(String categoria) {
        List<Prodotto> prodotti = prodottoDAO.trovaPerCategoria(categoria);
        List<ProdottoBean> catalogoBeans = new ArrayList<>();

        for (Prodotto p : prodotti) {
            ProdottoBean bean = elaboraProdottoRiepilogo(p);
            if (bean != null) {
                catalogoBeans.add(bean);
            }
        }
        return catalogoBeans;
    }

    private ProdottoBean elaboraProdottoRiepilogo(Prodotto p) {
        List<Lotto> lotti = lottoDAO.trovaPerProdotto(p.getNome());

        double minPrice = Double.MAX_VALUE;
        double maxPrice = 0.0;
        double validGiacenza = 0.0;
        boolean hasValidLots = false;

        for (Lotto l : lotti) {
            if (isLottoValido(l)) {
                hasValidLots = true;
                double prezzo = getPrezzoEffettivo(l);
                if (prezzo < minPrice)
                    minPrice = prezzo;
                if (prezzo > maxPrice)
                    maxPrice = prezzo;
                validGiacenza += l.getQuantitaKg();
            }
        }

        if (!hasValidLots || validGiacenza <= 0)
            return null;

        ProdottoBean bean = new ProdottoBean(
                p.getNome(),
                minPrice,
                validGiacenza,
                p.getCategoria(),
                p.getImmaginePath());
        bean.setPrezzoMin(minPrice);
        bean.setPrezzoMax(maxPrice);

        return bean;
    }

    private boolean isLottoValido(Lotto l) {
        return l.getStato() == it.ortogest.ortogestapp.model.StatoLotto.ATTIVO && l.getPrezzoVendita() > 0;
    }

    private double getPrezzoEffettivo(Lotto l) {
        return (l.isScontoScadenzaAttivo() && l.getPrezzoScontato() > 0) ? l.getPrezzoScontato() : l.getPrezzoVendita();
    }

    public List<Lotto> getLottiDisponibili(String nomeProdotto) {
        List<Lotto> lotti = lottoDAO.trovaPerProdotto(nomeProdotto);
        List<Lotto> lottiValidi = new ArrayList<>();
        for (Lotto l : lotti) {
            if (isLottoValido(l)) {
                lottiValidi.add(l);
            }
        }
        lottiValidi.sort(Comparator.comparing(Lotto::getDataScadenza));
        return lottiValidi;
    }

    /**
     * Crea un nuovo ordine a partire da una lista di prodotti nel carrello.
     * 
     * @param emailCliente L'email del cliente che effettua l'ordine.
     * @param carrello     La lista di prodotti e quantità selezionate.
     * @return Messaggio di conferma con l'ID dell'ordine.
     * @throws GestioneException Se un prodotto non è trovato o la giacenza è
     *                           insufficiente.
     */
    public String creaOrdine(String emailCliente, List<it.ortogest.ortogestapp.beans.RigaOrdineBean> carrello)
            throws GestioneException {
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
            // Creiamo la riga per il modello di dominio, passando anche idLotto
            righeModello.add(new it.ortogest.ortogestapp.model.RigaOrdine(
                    p.getNome(),
                    rigaBean.getIdLotto(),
                    rigaBean.getQuantita(),
                    rigaBean.getPrezzoUnitario()));
        }

        // Simula la creazione dell'ID univoco
        String idOrdine = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Creazione ordine
        Ordine nuovoOrdine = new Ordine(idOrdine, emailCliente, righeModello, "Inviato");

        // Salviamo nel DB
        ordineDAO.salvaOrdine(nuovoOrdine);

        // Scaliamo le giacenze per ogni prodotto nell'ordine applicando il FEFO sui
        // lotti
        for (it.ortogest.ortogestapp.model.RigaOrdine riga : righeModello) {
            Prodotto p = prodottoDAO.trovaPerNome(riga.getNomeProdotto());
            p.sottraiGiacenza(riga.getQuantita());
            prodottoDAO.salvaProdotto(p);

            // Scaliamo dal lotto esatto
            Lotto l = lottoDAO.trovaPerId(riga.getIdLotto());
            if (l != null && l.getQuantitaKg() >= riga.getQuantita()) {
                l.setQuantitaKg(l.getQuantitaKg() - riga.getQuantita());
                lottoDAO.salvaLotto(l);
            } else {
                // Se non c'è abbastanza quantità o non esiste, è un problema di concorrenza
                throw new GestioneException(
                        "Quantità non disponibile nel lotto selezionato per: " + riga.getNomeProdotto());
            }
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
                    o.getStato()));
        }
        return ordineBeans;
    }

    public void eliminaOrdine(String idOrdine) throws GestioneException {
        Ordine ordine = ordineDAO.trovaOrdinePerId(idOrdine);
        if (ordine == null) {
            throw new GestioneException("Ordine non trovato.");
        }

        if ("Pronto per il Ritiro".equals(ordine.getStato())) {
            throw new GestioneException("l'ordine è in attesa di ritiro dunque non può essere annullato");
        }

        // Se l'ordine è "Ritirato", lo eliminiamo (dallo storico del cliente) ma NON
        // ripristiniamo le giacenze.
        if (!"Ritirato".equals(ordine.getStato())) {
            // Ripristina le giacenze dei prodotti e dei lotti solo se non è ancora stato
            // ritirato
            for (it.ortogest.ortogestapp.model.RigaOrdine riga : ordine.getRighe()) {
                Prodotto p = prodottoDAO.trovaPerNome(riga.getNomeProdotto());
                if (p != null) {
                    p.aggiungiGiacenza(riga.getQuantita());
                    prodottoDAO.salvaProdotto(p);

                    // Ripristiniamo la giacenza nel lotto d'origine
                    Lotto l = lottoDAO.trovaPerId(riga.getIdLotto());
                    if (l != null) {
                        l.setQuantitaKg(l.getQuantitaKg() + riga.getQuantita());
                        lottoDAO.salvaLotto(l);
                    }
                }
            }
        }

        // Elimina l'ordine (e le righe associate) dal database
        ordineDAO.eliminaOrdine(idOrdine);
    }

    public List<OrdineBean> getTuttiOrdini() {
        List<Ordine> ordini = ordineDAO.trovaTuttiOrdini();
        List<OrdineBean> ordineBeans = new ArrayList<>();

        for (Ordine o : ordini) {
            ordineBeans.add(new OrdineBean(
                    o.getIdOrdine(),
                    o.getRiepilogoProdotti(),
                    o.getTotale(),
                    o.getStato(),
                    o.getEmailCliente()));
        }
        return ordineBeans;
    }

    public void aggiornaStatoOrdine(String idOrdine, String nuovoStato) {
        ordineDAO.aggiornaStatoOrdine(idOrdine, nuovoStato);
    }
}
