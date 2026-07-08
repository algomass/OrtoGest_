package it.ortogest.ortogestapp.graphiccontrollercli;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import it.ortogest.ortogestapp.appcontroller.CreaOrdineAppController;
import it.ortogest.ortogestapp.beans.OrdineBean;
import it.ortogest.ortogestapp.beans.ProdottoBean;
import it.ortogest.ortogestapp.beans.RigaOrdineBean;
import it.ortogest.ortogestapp.exception.GestioneException;
import it.ortogest.ortogestapp.beans.LottoBean;
import it.ortogest.ortogestapp.utils.Printer;
import it.ortogest.ortogestapp.utils.SessionManager;

public class ClienteGraphicControllerCLI extends BaseGraphicControllerCLI {

    private final CreaOrdineAppController appController;

    public ClienteGraphicControllerCLI() {
        this.appController = new CreaOrdineAppController();
        // Inizializza il carrello in sessione se non esiste
        if (SessionManager.getInstance().getCarrelloCorrente() == null) {
            SessionManager.getInstance().setCarrelloCorrente(new ArrayList<>());
        }
    }

    @Override
    public void start(Scanner scanner) {
        boolean exit = false;
        String userName = SessionManager.getInstance().getCurrentUser().getNome();

        while (!exit) {
            stampaMenu("AREA CLIENTI ORTOGEST",
                    "Benvenuto, " + userName,
                    "1. Sfoglia Catalogo e Acquista",
                    "2. Visualizza Carrello (" + getNumeroElementiCarrello() + " elementi)",
                    "3. Invia Ordine (Clicca & Ritira)",
                    "4. I Miei Ordini",
                    "5. Annulla un Ordine",
                    "0. Logout");
            String scelta = leggiStringaNonVuota(scanner, "Scelta: ");

            switch (scelta) {
                case "1":
                    sfogliaCatalogo(scanner);
                    break;
                case "2":
                    visualizzaCarrello();
                    break;
                case "3":
                    inviaOrdine();
                    break;
                case "4":
                    visualizzaStoricoOrdini();
                    break;
                case "5":
                    annullaOrdine(scanner);
                    break;
                case "0":
                    eseguiLogout();
                    exit = true;
                    break;
                default:
                    Printer.perror("Scelta non valida, riprova.");
            }
        }
    }

    private int getNumeroElementiCarrello() {
        List<RigaOrdineBean> carrello = SessionManager.getInstance().getCarrelloCorrente();
        return carrello == null ? 0 : carrello.size();
    }

    private void sfogliaCatalogo(Scanner scanner) {
        Printer.print("\n--- Catalogo Prodotti Disponibili ---");
        List<ProdottoBean> catalogo = appController.getCatalogoDisponibile();

        if (catalogo.isEmpty()) {
            Printer.print("Al momento non ci sono prodotti disponibili per l'acquisto.");
            return;
        }

        Printer.printf("%-5s %-15s %-15s %-15s\n", "NUM", "PRODOTTO", "PREZZO MIN", "PREZZO MAX");
        Printer.print("---------------------------------------------------------");
        for (int i = 0; i < catalogo.size(); i++) {
            ProdottoBean p = catalogo.get(i);
            Printer.printf("%-5d %-15s %-15.2f %-15.2f\n",
                    (i + 1),
                    p.getNome(),
                    p.getPrezzoMin(),
                    p.getPrezzoMax());
        }

        int sceltaProd = leggiInteroValido(scanner, "\nInserisci il NUM del prodotto che vuoi acquistare (oppure 0 per annullare): ", 0, catalogo.size());
        if (sceltaProd == 0) return;

        ProdottoBean scelto = catalogo.get(sceltaProd - 1);
        scegliLottoEQuantita(scelto, scanner);
    }

    private void scegliLottoEQuantita(ProdottoBean prodotto, Scanner scanner) {
        Printer.print("\nLotti disponibili per: " + prodotto.getNome());
        List<LottoBean> lotti = appController.getLottiDisponibili(prodotto.getNome());

        if (lotti.isEmpty()) {
            Printer.print("Nessun lotto disponibile.");
            return;
        }

        Printer.printf("%-5s %-15s %-15s %-15s %-15s\n", "NUM", "ID LOTTO", "SCADENZA", "GIACENZA", "PREZZO/KG");
        Printer.print("-------------------------------------------------------------------------");
        for (int i = 0; i < lotti.size(); i++) {
            LottoBean l = lotti.get(i);
            double prezzoEffettivo = l.isScontoScadenzaAttivo() && l.getPrezzoScontato() > 0 ? l.getPrezzoScontato()
                    : l.getPrezzoVendita();
            String infoSconto = l.isScontoScadenzaAttivo() ? " [SCONTATO]" : "";
            Printer.printf("%-5d %-15s %-15s %-15.2f %-15.2f %s\n",
                    (i + 1),
                    l.getIdLotto(),
                    l.getDataScadenza().toString(),
                    l.getQuantitaKg(),
                    prezzoEffettivo,
                    infoSconto);
        }

        int sceltaLotto = leggiInteroValido(scanner, "\nInserisci il NUM del lotto dal quale prelevare (oppure 0 per annullare): ", 0, lotti.size());
        if (sceltaLotto == 0) return;

        LottoBean lottoScelto = lotti.get(sceltaLotto - 1);
        double prezzoScelto = lottoScelto.isScontoScadenzaAttivo() && lottoScelto.getPrezzoScontato() > 0
                ? lottoScelto.getPrezzoScontato()
                : lottoScelto.getPrezzoVendita();

        double quantita = leggiDoubleValido(scanner, "Quantità da acquistare (Kg) (Max " + lottoScelto.getQuantitaKg() + "): ", 0.01);

        if (quantita > lottoScelto.getQuantitaKg()) {
            Printer.perror("Quantità superiore alla giacenza.");
            return;
        }

        // Creiamo la riga d'ordine e l'aggiungiamo al carrello
        RigaOrdineBean riga = new RigaOrdineBean(prodotto.getNome(), lottoScelto.getIdLotto(), quantita, prezzoScelto);
        SessionManager.getInstance().getCarrelloCorrente().add(riga);
        Printer.print("[SUCCESS] Prodotto aggiunto al carrello!");
    }

    private void visualizzaCarrello() {
        Printer.print("\n--- Carrello Corrente ---");
        List<RigaOrdineBean> carrello = SessionManager.getInstance().getCarrelloCorrente();

        if (carrello == null || carrello.isEmpty()) {
            Printer.print("Il carrello è vuoto.");
            return;
        }

        double totale = 0;
        Printer.printf("%-15s %-15s %-15s %-15s\n", "PRODOTTO", "QUANTITA (Kg)", "PREZZO/Kg", "SUBTOTALE");
        Printer.print("-----------------------------------------------------------------");
        for (RigaOrdineBean riga : carrello) {
            Printer.printf("%-15s %-15.2f %-15.2f %-15.2f\n",
                    riga.getNomeProdotto(),
                    riga.getQuantita(),
                    riga.getPrezzoUnitario(),
                    riga.getSubtotale());
            totale += riga.getSubtotale();
        }
        Printer.print("-----------------------------------------------------------------");
        Printer.printf("TOTALE: %.2f EUR\n", totale);
    }

    private void inviaOrdine() {
        Printer.print("\n--- Check-out Ordine ---");
        List<RigaOrdineBean> carrello = SessionManager.getInstance().getCarrelloCorrente();

        if (carrello == null || carrello.isEmpty()) {
            Printer.perror("Non puoi inviare un ordine vuoto.");
            return;
        }

        String emailUtente = SessionManager.getInstance().getCurrentUser().getEmail();

        try {
            String risultato = appController.creaOrdine(emailUtente, carrello);
            Printer.print("[SUCCESS] " + risultato);
            // Svuota carrello dopo il completamento
            carrello.clear();
        } catch (it.ortogest.ortogestapp.exception.ValidationException | it.ortogest.ortogestapp.exception.InsufficientStockException | it.ortogest.ortogestapp.exception.ItemNotFoundException e) {
            Printer.perror("[ERRORE] Impossibile creare l'ordine: " + e.getMessage());
        } catch (GestioneException e) {
            Printer.perror("[ERRORE SISTEMA] " + e.getMessage());
        }
    }

    private void visualizzaStoricoOrdini() {
        Printer.print("\n--- Storico Miei Ordini ---");
        String emailUtente = SessionManager.getInstance().getCurrentUser().getEmail();
        List<OrdineBean> ordini = appController.getOrdiniCliente(emailUtente);

        if (ordini.isEmpty()) {
            Printer.print("Non hai effettuato nessun ordine.");
            return;
        }

        for (OrdineBean o : ordini) {
            Printer.print("\nID Ordine: " + o.getIdOrdine() + " | Stato: " + o.getStato() + " | Totale: "
                    + o.getTotale() + " EUR");
            Printer.print("Contenuto: " + o.getRiepilogoProdotti());
        }
    }

    private void annullaOrdine(Scanner scanner) {
        visualizzaStoricoOrdini();
        String idOrdine = leggiStringaOpzionale(scanner, "\nInserisci l'ID dell'ordine da annullare (oppure INVIO vuoto per uscire): ");

        if (idOrdine.isEmpty())
            return;

        try {
            appController.eliminaOrdine(idOrdine);
            Printer.print("[SUCCESS] Ordine " + idOrdine + " annullato correttamente.");
        } catch (it.ortogest.ortogestapp.exception.InvalidStateException | it.ortogest.ortogestapp.exception.ItemNotFoundException e) {
            Printer.perror("[ERRORE] " + e.getMessage());
        } catch (GestioneException e) {
            Printer.perror("[ERRORE DI SISTEMA] " + e.getMessage());
        }
    }
}
