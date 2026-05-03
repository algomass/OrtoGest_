package it.ortogest.ortogestapp.appcontroller;

import it.ortogest.ortogestapp.beans.AnomaliaBean;
import it.ortogest.ortogestapp.pattern.DummyEmailService;
import it.ortogest.ortogestapp.pattern.EmailAdapter;
import it.ortogest.ortogestapp.beans.LottoBean;
import it.ortogest.ortogestapp.beans.ProdottoBean;
import it.ortogest.ortogestapp.dao.DAOFactory;
import it.ortogest.ortogestapp.dao.ILottoDAO;
import it.ortogest.ortogestapp.dao.IProdottoDAO;
import it.ortogest.ortogestapp.model.Lotto;
import it.ortogest.ortogestapp.model.Prodotto;

import java.util.ArrayList;
import java.util.List;

/**
 * Application Controller per la gestione delle operazioni di Magazzino.
 * Coordinerà l'inserimento dei lotti e le segnalazioni di anomalie.
 */
public class GestioneMagazzinoAppController {

    // Utilizziamo l'interfaccia Adapter per l'invio delle email
    private EmailAdapter emailAdapter;
    private ILottoDAO lottoDAO;
    private IProdottoDAO prodottoDAO;

    public GestioneMagazzinoAppController() {
        // In futuro potremmo usare la Dependency Injection (es. tramite un Factory)
        // per instanziare il servizio reale. Per ora usiamo il Dummy.
        this.emailAdapter = new DummyEmailService();
        this.lottoDAO = DAOFactory.getInstance().getLottoDAO();
        this.prodottoDAO = DAOFactory.getInstance().getProdottoDAO();
    }

    public List<ProdottoBean> getInventario() {
        List<Prodotto> prodotti = prodottoDAO.getTuttiIProdotti();
        List<ProdottoBean> beans = new ArrayList<>();
        for (Prodotto p : prodotti) {
            beans.add(new ProdottoBean(p.getNome(), p.getPrezzoAttuale(), p.getQuantitaTotaleDisponibile(), p.getCategoria(), p.getImmaginePath()));
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

    /**
     * Segnala un'anomalia di fornitura (merce mancante o danneggiata) e invia una mail al fornitore.
     * @param anomaliaBean I dati inseriti dalla vista.
     * @return Messaggio di conferma o errore.
     */
    public String inoltraSegnalazione(AnomaliaBean anomaliaBean) {
        
        // 1. (Opzionale/Futuro) Salvare l'anomalia nel DB usando un DAO appropriato.

        // 2. Costruire il messaggio dell'email
        // Nella realtà, l'email del fornitore verrebbe recuperata dal DB tramite il FornitoreDAO 
        // associato a quel lotto/prodotto. Per ora simuliamo.
        String emailFornitore = "assistenza@fornitore-ortofrutta.it";
        String oggetto = "Segnalazione Anomalia: Merce " + anomaliaBean.getTipoAnomalia();
        
        String corpo = String.format("Spett.le Fornitore,\n\n" +
                "Con la presente segnaliamo un'anomalia relativa all'ultima consegna.\n" +
                "Prodotto: %s\n" +
                "Quantità %s: %.2f Kg\n" +
                "Note aggiuntive: %s\n\n" +
                "In attesa di un vostro riscontro, porgiamo cordiali saluti.\n" +
                "Il team di OrtoGest.", 
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
    
    public LottoBean registraLotto(LottoBean bean) throws Exception {
        // 1. Validazione base
        if (bean.getIdLotto() == null || bean.getIdLotto().trim().isEmpty()) {
            throw new Exception("L'ID del Lotto non può essere vuoto.");
        }
        if (bean.getQuantitaKg() <= 0) {
            throw new Exception("La quantità deve essere maggiore di zero.");
        }
        if (bean.getDataArrivo() == null || bean.getDataScadenza() == null) {
            throw new Exception("Le date di arrivo e scadenza sono obbligatorie.");
        }
        if (bean.getDataScadenza().isBefore(bean.getDataArrivo())) {
            throw new Exception("La data di scadenza non può essere precedente alla data di arrivo.");
        }

        // 2. Controllo duplicato del lotto
        if (lottoDAO.trovaPerId(bean.getIdLotto()) != null) {
            throw new Exception("Esiste già un lotto registrato con ID: " + bean.getIdLotto());
        }

        // 3. Ricerca del prodotto
        Prodotto prodotto = prodottoDAO.trovaPerNome(bean.getNomeProdotto());
        
        if (prodotto == null) {
            // Creiamo un nuovo prodotto dinamicamente se non esiste
            // Categoria di default "Frutta" — il responsabile potrà modificarla in seguito
            prodotto = new Prodotto(bean.getNomeProdotto(), 0.0, 0.0, "Frutta", "/images/placeholder.png");
            prodottoDAO.salvaProdotto(prodotto);
        }

        // 4. Creazione dell'Entità Lotto
        Lotto lotto = new Lotto(
                bean.getIdLotto(),
                bean.getNomeFornitore(),
                prodotto,
                bean.getQuantitaKg(),
                bean.getDataArrivo(),
                bean.getDataScadenza(),
                bean.getCostoAcquisto()
        );

        // 5. Aggiornamento Giacenza Prodotto
        prodotto.aggiungiGiacenza(lotto.getQuantitaKg());

        // 6. Salvataggio
        lottoDAO.salvaLotto(lotto);

        // 7. Ritorno Bean per conferma
        return bean;
    }
}
