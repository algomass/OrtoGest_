package it.ortogest.ortogestapp.appcontroller;

import it.ortogest.ortogestapp.beans.LottoBean;
import it.ortogest.ortogestapp.beans.OrdineBean;
import it.ortogest.ortogestapp.beans.ProdottoBean;
import it.ortogest.ortogestapp.dao.interfacedao.ILottoDAO;
import it.ortogest.ortogestapp.dao.interfacedao.IOrdineDAO;
import it.ortogest.ortogestapp.dao.interfacedao.IProdottoDAO;
import it.ortogest.ortogestapp.exception.GestioneException;
import it.ortogest.ortogestapp.exception.ValidationException;
import it.ortogest.ortogestapp.exception.ItemNotFoundException;
import it.ortogest.ortogestapp.exception.InsufficientStockException;
import it.ortogest.ortogestapp.exception.InvalidStateException;
import it.ortogest.ortogestapp.model.Lotto;
import it.ortogest.ortogestapp.model.Ordine;
import it.ortogest.ortogestapp.model.Prodotto;
import it.ortogest.ortogestapp.pattern.abstractfactory.DAOFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Use Case Controller per la creazione e gestione degli ordini (Cliente e Responsabile).
 */
public class CreaOrdineAppController {

    private static final String STATO_PRONTO_RITIRO = "Pronto per il Ritiro";

    private IProdottoDAO prodottoDAO;
    private IOrdineDAO ordineDAO;
    private ILottoDAO lottoDAO;

    public CreaOrdineAppController() {
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

    public List<LottoBean> getLottiDisponibili(String nomeProdotto) {
        List<Lotto> lotti = lottoDAO.trovaPerProdotto(nomeProdotto);
        List<LottoBean> lottiValidi = new ArrayList<>();
        for (Lotto l : lotti) {
            if (isLottoValido(l)) {
                LottoBean bean = LottoBean.builder()
                        .nomeProdotto(nomeProdotto)
                        .idLotto(l.getIdLotto())
                        .nomeFornitore(l.getNomeFornitore())
                        .quantitaKg(l.getQuantitaKg())
                        .dataArrivo(l.getDataArrivo())
                        .dataScadenza(l.getDataScadenza())
                        .costoAcquisto(l.getCostoAcquisto())
                        .prezzoVendita(l.getPrezzoVendita())
                        .scontoScadenzaAttivo(l.isScontoScadenzaAttivo())
                        .prezzoScontato(l.getPrezzoScontato())
                        .smaltito(l.isSmaltito())
                        .ritirato(l.isRitirato())
                        .build();
                lottiValidi.add(bean);
            }
        }
        lottiValidi.sort(Comparator.comparing(LottoBean::getDataScadenza));
        return lottiValidi;
    }

    public String creaOrdine(String emailCliente, List<it.ortogest.ortogestapp.beans.RigaOrdineBean> carrello)
            throws GestioneException {
        if (carrello == null || carrello.isEmpty()) {
            throw new ValidationException("Il carrello è vuoto.");
        }

        List<it.ortogest.ortogestapp.model.RigaOrdine> righeModello = new ArrayList<>();

        for (it.ortogest.ortogestapp.beans.RigaOrdineBean rigaBean : carrello) {
            Prodotto p = prodottoDAO.trovaPerNome(rigaBean.getNomeProdotto());
            if (p == null) {
                throw new ItemNotFoundException("Prodotto '" + rigaBean.getNomeProdotto() + "' non trovato.");
            }
            if (p.getQuantitaTotaleDisponibile() < rigaBean.getQuantita()) {
                throw new InsufficientStockException("Giacenza insufficiente per: " + rigaBean.getNomeProdotto());
            }
            righeModello.add(new it.ortogest.ortogestapp.model.RigaOrdine(
                    p.getNome(),
                    rigaBean.getIdLotto(),
                    rigaBean.getQuantita(),
                    rigaBean.getPrezzoUnitario()));
        }

        String idOrdine = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Ordine nuovoOrdine = new Ordine(idOrdine, emailCliente, righeModello, "Inviato");

        ordineDAO.salvaOrdine(nuovoOrdine);

        for (it.ortogest.ortogestapp.model.RigaOrdine riga : righeModello) {
            Prodotto p = prodottoDAO.trovaPerNome(riga.getNomeProdotto());
            p.sottraiGiacenza(riga.getQuantita());
            prodottoDAO.salvaProdotto(p);

            Lotto l = lottoDAO.trovaPerId(riga.getIdLotto());
            if (l != null && l.getQuantitaKg() >= riga.getQuantita()) {
                l.setQuantitaKg(l.getQuantitaKg() - riga.getQuantita());
                lottoDAO.salvaLotto(l);
            } else {
                throw new InsufficientStockException(
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
            throw new ItemNotFoundException("Ordine non trovato.");
        }

        if (STATO_PRONTO_RITIRO.equals(ordine.getStato())) {
            throw new InvalidStateException("l'ordine è in attesa di ritiro dunque non può essere annullato");
        }

        if (!"Ritirato".equals(ordine.getStato())) {
            for (it.ortogest.ortogestapp.model.RigaOrdine riga : ordine.getRighe()) {
                Prodotto p = prodottoDAO.trovaPerNome(riga.getNomeProdotto());
                if (p != null) {
                    p.aggiungiGiacenza(riga.getQuantita());
                    prodottoDAO.salvaProdotto(p);

                    Lotto l = lottoDAO.trovaPerId(riga.getIdLotto());
                    if (l != null) {
                        l.setQuantitaKg(l.getQuantitaKg() + riga.getQuantita());
                        lottoDAO.salvaLotto(l);
                    }
                }
            }
        }

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

    /**
     * Recupera la lista degli ordini che sono stati preparati e attendono solo il pagamento in cassa.
     */
    public List<OrdineBean> getOrdiniProntiPerRitiro() {
        List<Ordine> tuttiOrdini = ordineDAO.trovaTuttiOrdini();
        List<OrdineBean> pronti = new ArrayList<>();

        for (Ordine o : tuttiOrdini) {
            if (STATO_PRONTO_RITIRO.equals(o.getStato())) {
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
            throw new ItemNotFoundException("Ordine non trovato.");
        }
        if (!STATO_PRONTO_RITIRO.equals(ordine.getStato())) {
            throw new InvalidStateException("L'ordine non è pronto per il ritiro.");
        }

        ordineDAO.aggiornaStatoOrdine(idOrdine, "Ritirato");
    }
}
