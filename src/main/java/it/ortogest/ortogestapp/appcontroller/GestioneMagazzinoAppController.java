package it.ortogest.ortogestapp.appcontroller;

import it.ortogest.ortogestapp.beans.AnomaliaBean;
import it.ortogest.ortogestapp.beans.LottoBean;
import it.ortogest.ortogestapp.beans.ProdottoBean;
import it.ortogest.ortogestapp.dao.interfacedao.ILottoDAO;
import it.ortogest.ortogestapp.dao.interfacedao.IProdottoDAO;
import it.ortogest.ortogestapp.exception.GestioneException;
import it.ortogest.ortogestapp.model.CategoriaProdotto;
import it.ortogest.ortogestapp.model.Lotto;
import it.ortogest.ortogestapp.model.Prodotto;
import it.ortogest.ortogestapp.pattern.abstractfactory.DAOFactory;
import it.ortogest.ortogestapp.pattern.adapter.ApacheCommonsEmailAdapter;
import it.ortogest.ortogestapp.pattern.adapter.EmailTarget;

import java.util.ArrayList;
import java.util.List;

/**
 * Application Controller per la gestione delle operazioni di Magazzino.
 * Coordinerà l'inserimento dei lotti e le segnalazioni di anomalie.
 */
public class GestioneMagazzinoAppController {

    // Utilizziamo l'interfaccia Adapter per l'invio delle email
    private EmailTarget emailAdapter;
    private ILottoDAO lottoDAO;
    private IProdottoDAO prodottoDAO;

    private static final String EMAIL_FORNITORE_DEFAULT = "assistenza@fornitore-ortofrutta.it";

    public GestioneMagazzinoAppController() {
        // In futuro potremmo usare la Dependency Injection (es. tramite un Factory)
        // per instanziare il servizio reale. Per ora usiamo l'Adapter reale.
        this.emailAdapter = new ApacheCommonsEmailAdapter();
        this.lottoDAO = DAOFactory.getInstance().getLottoDAO();
        this.prodottoDAO = DAOFactory.getInstance().getProdottoDAO();
    }

    public List<ProdottoBean> getInventario() {
        List<Prodotto> prodotti = prodottoDAO.getTuttiIProdotti();
        List<ProdottoBean> beans = new ArrayList<>();
        for (Prodotto p : prodotti) {
            // Mostra solo i prodotti che hanno almeno un lotto registrato
            List<Lotto> lotti = lottoDAO.trovaPerProdotto(p.getNome());
            if (!lotti.isEmpty()) {
                ProdottoBean bean = new ProdottoBean(p.getNome(), p.getPrezzoAttuale(),
                        p.getQuantitaTotaleDisponibile(), p.getCategoria(), p.getImmaginePath());
                bean.setPrezzoAcquistoMedio(lottoDAO.getPrezzoMedioAcquisto(p.getNome()));
                beans.add(bean);
            }
        }
        return beans;
    }

    public List<String> getNomiProdotti() {
        List<Prodotto> prodotti = prodottoDAO.getTuttiIProdotti();
        List<String> nomi = new ArrayList<>();
        for (Prodotto p : prodotti) {
            nomi.add(p.getNome());
        }
        return nomi;
    }

    public List<LottoBean> getLottiPerProdotto(String nomeProdotto) {
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
                    .build());
        }
        return beans;
    }

    /**
     * Segnala un'anomalia di fornitura (merce mancante o danneggiata) e invia una
     * mail al fornitore.
     * 
     * @param anomaliaBean I dati inseriti dalla vista.
     * @return Messaggio di conferma o errore.
     */
    public String inoltraSegnalazione(AnomaliaBean anomaliaBean) {

        // Costruire il messaggio dell'email
        // Nella realtà, l'email del fornitore verrebbe recuperata dal DB tramite il
        // FornitoreDAO
        // associato a quel lotto/prodotto. Per ora simuliamo.
        String emailFornitore = EMAIL_FORNITORE_DEFAULT;
        String oggetto = "Segnalazione Anomalia: Merce " + anomaliaBean.getTipoAnomalia();

        String corpo = String.format("""
                Spett.le Fornitore,

                Con la presente segnaliamo un'anomalia relativa all'ultima consegna.
                Prodotto: %s
                Quantità %s: %.2f Kg
                Note aggiuntive: %s

                In attesa di un vostro riscontro, porgiamo cordiali saluti.
                Il team di OrtoGest.""",
                anomaliaBean.getNomeProdotto(),
                anomaliaBean.getTipoAnomalia().toLowerCase(),
                anomaliaBean.getQuantita(),
                anomaliaBean.getNote());

        // 3. Inviare l'email tramite l'Adapter
        boolean successo = emailAdapter.inviaEmail(emailFornitore, oggetto, corpo);

        if (successo) {
            return "Segnalazione inoltrata con successo via Mail.";
        } else {
            return "Errore di connessione al server Mail. Riprovare più tardi.";
        }
    }

    public LottoBean registraLotto(LottoBean bean) throws GestioneException {
        // 1. Validazione base
        if (bean.getIdLotto() == null || bean.getIdLotto().trim().isEmpty()) {
            throw new GestioneException("L'ID del Lotto non può essere vuoto.");
        }
        if (bean.getQuantitaKg() <= 0) {
            throw new GestioneException("La quantità deve essere maggiore di zero.");
        }
        if (bean.getDataArrivo() == null || bean.getDataScadenza() == null) {
            throw new GestioneException("Le date di arrivo e scadenza sono obbligatorie.");
        }
        if (bean.getDataScadenza().isBefore(bean.getDataArrivo())) {
            throw new GestioneException("La data di scadenza non può essere precedente alla data di arrivo.");
        }

        // 2. Controllo duplicato del lotto
        if (lottoDAO.trovaPerId(bean.getIdLotto()) != null) {
            throw new GestioneException("Esiste già un lotto registrato con ID: " + bean.getIdLotto());
        }

        // 3. Ricerca del prodotto
        Prodotto prodotto = prodottoDAO.trovaPerNome(bean.getNomeProdotto());

        if (prodotto == null) {
            // Creiamo un nuovo prodotto dinamicamente se non esiste
            // Categoria di default FRUTTA Ã¢â‚¬âEUR�? il responsabile potrà modificarla in seguito
            prodotto = new Prodotto(bean.getNomeProdotto(), 0.0, 0.0, CategoriaProdotto.FRUTTA,
                    "/images/placeholder.png");
            prodottoDAO.salvaProdotto(prodotto);
        }

        // 4. Creazione dell'Entità Lotto
        Lotto lotto = Lotto.builder()
                .idLotto(bean.getIdLotto())
                .nomeFornitore(bean.getNomeFornitore())
                .tipologiaProdotto(prodotto)
                .quantitaKg(bean.getQuantitaKg())
                .dataArrivo(bean.getDataArrivo())
                .dataScadenza(bean.getDataScadenza())
                .costoAcquisto(bean.getCostoAcquisto())
                .build();

        // 5. Aggiornamento Giacenza Prodotto
        prodotto.aggiungiGiacenza(lotto.getQuantitaKg());
        prodottoDAO.salvaProdotto(prodotto); // Salvataggio aggiornamento prodotto

        // 6. Salvataggio Lotto
        lottoDAO.salvaLotto(lotto);

        // 7. Ritorno Bean per conferma
        return bean;
    }

    public void eliminaLotto(String idLotto) throws GestioneException {
        Lotto lotto = lottoDAO.trovaPerId(idLotto);
        if (lotto == null) {
            throw new GestioneException("Lotto non trovato.");
        }

        Prodotto prodotto = lotto.getTipologiaProdotto();

        // Verifica se è l'ultimo lotto per questo prodotto
        List<Lotto> lottiEsistenti = lottoDAO.trovaPerProdotto(prodotto.getNome());
        boolean isUltimoLotto = (lottiEsistenti.size() == 1 && lottiEsistenti.get(0).getIdLotto().equals(idLotto));

        lottoDAO.eliminaLotto(idLotto);

        // Sottrai sempre la quantità, assicurandoti che non diventi negativa
        prodotto.sottraiGiacenza(lotto.getQuantitaKg());
        if (prodotto.getQuantitaTotaleDisponibile() < 0) {
            prodotto.setQuantitaTotaleDisponibile(0);
        }
        prodottoDAO.salvaProdotto(prodotto);

        if (isUltimoLotto) {
            // Elimina il prodotto dalla tabella principale se era l'ultimo lotto
            prodottoDAO.eliminaProdotto(prodotto.getNome());
        }
    }

    public void modificaLotto(LottoBean beanNuovo) throws GestioneException {
        Lotto lottoVecchio = lottoDAO.trovaPerId(beanNuovo.getIdLotto());
        if (lottoVecchio == null) {
            throw new GestioneException("Lotto non trovato.");
        }

        double diff = beanNuovo.getQuantitaKg() - lottoVecchio.getQuantitaKg();

        Prodotto prodotto = lottoVecchio.getTipologiaProdotto();
        prodotto.aggiungiGiacenza(diff);

        // Evita che la giacenza diventi negativa
        if (prodotto.getQuantitaTotaleDisponibile() < 0) {
            prodotto.setQuantitaTotaleDisponibile(0);
        }
        prodottoDAO.salvaProdotto(prodotto);

        lottoVecchio.setNomeFornitore(beanNuovo.getNomeFornitore());
        lottoVecchio.setQuantitaKg(beanNuovo.getQuantitaKg());
        lottoVecchio.setDataArrivo(beanNuovo.getDataArrivo());
        lottoVecchio.setDataScadenza(beanNuovo.getDataScadenza());
        lottoVecchio.setCostoAcquisto(beanNuovo.getCostoAcquisto());

        lottoDAO.salvaLotto(lottoVecchio);
    }
}
