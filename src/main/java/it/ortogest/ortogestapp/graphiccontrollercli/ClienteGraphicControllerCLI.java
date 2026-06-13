package it.ortogest.ortogestapp.graphiccontrollercli;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import it.ortogest.ortogestapp.appcontroller.GestioneOrdiniAppController;
import it.ortogest.ortogestapp.beans.OrdineBean;
import it.ortogest.ortogestapp.beans.ProdottoBean;
import it.ortogest.ortogestapp.beans.RigaOrdineBean;
import it.ortogest.ortogestapp.exception.GestioneException;
import it.ortogest.ortogestapp.model.Lotto;
import it.ortogest.ortogestapp.utils.Printer;
import it.ortogest.ortogestapp.utils.SessionManager;

public class ClienteGraphicControllerCLI implements GraphicControllerCLI {

    private final GestioneOrdiniAppController appController;

    public ClienteGraphicControllerCLI() {
        this.appController = new GestioneOrdiniAppController();
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
            Printer.print("\n=================================");
            Printer.print("       AREA CLIENTI ORTOGEST     ");
            Printer.print("       Benvenuto, " + userName);
            Printer.print("=================================");
            Printer.print("1. Sfoglia Catalogo e Acquista");
            Printer.print("2. Visualizza Carrello (" + getNumeroElementiCarrello() + " elementi)");
            Printer.print("3. Invia Ordine (Clicca & Ritira)");
            Printer.print("4. I Miei Ordini");
            Printer.print("5. Annulla un Ordine");
            Printer.print("0. Logout");
            Printer.print("Scelta: ");

            String scelta = scanner.nextLine();

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
                    Printer.print("Logout in corso...");
                    // Rimuoviamo il carrello alla disconnessione
                    SessionManager.getInstance().setCarrelloCorrente(null);
                    SessionManager.getInstance().logout();
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

        Printer.print("\nInserisci il NUM del prodotto che vuoi acquistare (oppure 0 per annullare): ");
        try {
            int sceltaProd = Integer.parseInt(scanner.nextLine());
            if (sceltaProd == 0) return;
            if (sceltaProd < 1 || sceltaProd > catalogo.size()) {
                Printer.perror("Scelta non valida.");
                return;
            }

            ProdottoBean scelto = catalogo.get(sceltaProd - 1);
            scegliLottoEQuantita(scelto, scanner);

        } catch (NumberFormatException _) {
            Printer.perror("Inserire un numero valido.");
        }
    }

    private void scegliLottoEQuantita(ProdottoBean prodotto, Scanner scanner) {
        Printer.print("\nLotti disponibili per: " + prodotto.getNome());
        List<Lotto> lotti = appController.getLottiDisponibili(prodotto.getNome());

        if (lotti.isEmpty()) {
            Printer.print("Nessun lotto disponibile.");
            return;
        }

        Printer.printf("%-5s %-15s %-15s %-15s %-15s\n", "NUM", "ID LOTTO", "SCADENZA", "GIACENZA", "PREZZO/KG");
        Printer.print("-------------------------------------------------------------------------");
        for (int i = 0; i < lotti.size(); i++) {
            Lotto l = lotti.get(i);
            double prezzoEffettivo = l.isScontoScadenzaAttivo() && l.getPrezzoScontato() > 0 ? l.getPrezzoScontato() : l.getPrezzoVendita();
            String infoSconto = l.isScontoScadenzaAttivo() ? " [SCONTATO]" : "";
            Printer.printf("%-5d %-15s %-15s %-15.2f %-15.2f %s\n", 
                (i + 1), 
                l.getIdLotto(), 
                l.getDataScadenza().toString(), 
                l.getQuantitaKg(), 
                prezzoEffettivo,
                infoSconto);
        }

        Printer.print("\nInserisci il NUM del lotto dal quale prelevare (oppure 0 per annullare): ");
        try {
            int sceltaLotto = Integer.parseInt(scanner.nextLine());
            if (sceltaLotto == 0) return;
            if (sceltaLotto < 1 || sceltaLotto > lotti.size()) {
                Printer.perror("Scelta non valida.");
                return;
            }

            Lotto lottoScelto = lotti.get(sceltaLotto - 1);
            double prezzoScelto = lottoScelto.isScontoScadenzaAttivo() && lottoScelto.getPrezzoScontato() > 0 ? lottoScelto.getPrezzoScontato() : lottoScelto.getPrezzoVendita();

            Printer.print("Quantità da acquistare (Kg) (Max " + lottoScelto.getQuantitaKg() + "): ");
            double quantita = Double.parseDouble(scanner.nextLine());

            if (quantita <= 0 || quantita > lottoScelto.getQuantitaKg()) {
                Printer.perror("Quantità non valida o superiore alla giacenza.");
                return;
            }

            // Creiamo la riga d'ordine e l'aggiungiamo al carrello
            RigaOrdineBean riga = new RigaOrdineBean(prodotto.getNome(), lottoScelto.getIdLotto(), quantita, prezzoScelto);
            SessionManager.getInstance().getCarrelloCorrente().add(riga);
            Printer.print("[SUCCESS] Prodotto aggiunto al carrello!");

        } catch (NumberFormatException _) {
            Printer.perror("Inserire un valore numerico valido.");
        }
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
        Printer.printf("TOTALE: %.2f â‚¬\n", totale);
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
        } catch (GestioneException e) {
            Printer.perror("[ERRORE] Impossibile creare l'ordine: " + e.getMessage());
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
            Printer.print("\nID Ordine: " + o.getIdOrdine() + " | Stato: " + o.getStato() + " | Totale: " + o.getTotale() + " â‚¬");
            Printer.print("Contenuto: " + o.getRiepilogoProdotti());
        }
    }

    private void annullaOrdine(Scanner scanner) {
        visualizzaStoricoOrdini();
        Printer.print("\nInserisci l'ID dell'ordine da annullare (oppure INVIO vuoto per uscire): ");
        String idOrdine = scanner.nextLine().trim();

        if (idOrdine.isEmpty()) return;

        try {
            appController.eliminaOrdine(idOrdine);
            Printer.print("[SUCCESS] Ordine " + idOrdine + " annullato correttamente.");
        } catch (GestioneException e) {
            Printer.perror("[ERRORE] " + e.getMessage());
        }
    }
}
