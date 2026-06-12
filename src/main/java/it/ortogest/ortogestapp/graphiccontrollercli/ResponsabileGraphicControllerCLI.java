package it.ortogest.ortogestapp.graphiccontrollercli;

import java.util.List;
import java.util.Scanner;

import it.ortogest.ortogestapp.appcontroller.GestioneCatalogoAppController;
import it.ortogest.ortogestapp.beans.LottoBean;
import it.ortogest.ortogestapp.beans.ProdottoBean;
import it.ortogest.ortogestapp.exception.GestioneException;
import it.ortogest.ortogestapp.utils.Printer;
import it.ortogest.ortogestapp.utils.SessionManager;

public class ResponsabileGraphicControllerCLI implements GraphicControllerCLI {

    private final GestioneCatalogoAppController appController;

    public ResponsabileGraphicControllerCLI() {
        this.appController = new GestioneCatalogoAppController();
    }

    @Override
    public void start(Scanner scanner) {
        boolean exit = false;
        String userName = SessionManager.getInstance().getCurrentUser().getNome();

        while (!exit) {
            Printer.print("\n=================================");
            Printer.print("     DASHBOARD RESPONSABILE      ");
            Printer.print("     Bentornato, " + userName);
            Printer.print("=================================");
            Printer.print("1. Visualizza Catalogo Completo");
            Printer.print("2. Imposta/Modifica Prezzo di Vendita Lotto");
            Printer.print("3. Monitora Scadenze e Applica Sconti");
            Printer.print("4. Aggiorna Categoria Prodotto");
            Printer.print("0. Logout");
            Printer.print("Scelta: ");

            String scelta = scanner.nextLine();

            switch (scelta) {
                case "1":
                    visualizzaCatalogo();
                    break;
                case "2":
                    modificaPrezzoLotto(scanner);
                    break;
                case "3":
                    applicaScontiScadenza(scanner);
                    break;
                case "4":
                    aggiornaCategoria(scanner);
                    break;
                case "0":
                    Printer.print("Logout in corso...");
                    SessionManager.getInstance().logout();
                    exit = true;
                    break;
                default:
                    Printer.perror("Scelta non valida, riprova.");
            }
        }
    }

    private List<ProdottoBean> visualizzaCatalogo() {
        Printer.print("\n--- Catalogo Completo ---");
        List<ProdottoBean> catalogo = appController.getTuttiIProdotti();

        if (catalogo.isEmpty()) {
            Printer.print("Il catalogo è vuoto.");
            return catalogo;
        }

        Printer.printf("%-5s %-15s %-15s %-15s %-15s\n", "NUM", "PRODOTTO", "CATEGORIA", "GIACENZA TOT", "COSTO MEDIO");
        Printer.print("-----------------------------------------------------------------");
        for (int i = 0; i < catalogo.size(); i++) {
            ProdottoBean p = catalogo.get(i);
            Printer.printf("%-5d %-15s %-15s %-15.2f %-15.2f â‚¬\n",
                    (i+1),
                    p.getNome(),
                    p.getCategoria(),
                    p.getGiacenza(),
                    p.getPrezzoAcquistoMedio());
        }
        return catalogo;
    }

    private void modificaPrezzoLotto(Scanner scanner) {
        List<ProdottoBean> catalogo = visualizzaCatalogo();
        if (catalogo.isEmpty()) return;

        Printer.print("\nInserisci il NUM del Prodotto per visualizzarne i lotti (oppure 0 per annullare): ");
        try {
            int numProd = Integer.parseInt(scanner.nextLine().trim());
            if (numProd == 0) return;
            if (numProd < 1 || numProd > catalogo.size()) {
                Printer.perror("Numero non valido.");
                return;
            }
            String nomeProdotto = catalogo.get(numProd - 1).getNome();

            List<LottoBean> lotti = appController.getLottiPerProdotto(nomeProdotto);

            if (lotti.isEmpty()) {
                Printer.perror("Nessun lotto trovato per questo prodotto.");
                return;
            }

            Printer.printf("%-5s %-15s %-15s %-15s %-15s\n", "NUM", "ID LOTTO", "COSTO ACQ.", "PREZZO VENDITA", "SCONTO ATTIVO");
            Printer.print("-------------------------------------------------------------------");
            for (int i = 0; i < lotti.size(); i++) {
                LottoBean l = lotti.get(i);
                Printer.printf("%-5d %-15s %-15.2f â‚¬ %-15.2f â‚¬ %-15s\n",
                        (i+1),
                        l.getIdLotto(),
                        l.getCostoAcquisto(),
                        l.getPrezzoVendita(),
                        l.isScontoScadenzaAttivo() ? "SI (" + l.getPrezzoScontato() + " â‚¬)" : "NO");
            }

            Printer.print("\nInserisci il NUM del lotto da modificare (oppure 0 per annullare): ");
            int numLotto = Integer.parseInt(scanner.nextLine().trim());
            if (numLotto == 0) return;
            if (numLotto < 1 || numLotto > lotti.size()) {
                Printer.perror("Numero non valido.");
                return;
            }

            LottoBean lottoScelto = lotti.get(numLotto - 1);

            Printer.print("Attuale costo di acquisto: " + lottoScelto.getCostoAcquisto() + " â‚¬");
            Printer.print("Nuovo Prezzo di Vendita al pubblico (â‚¬): ");
            double nuovoPrezzo = Double.parseDouble(scanner.nextLine());

            // Il bean viene usato come trasportatore dei dati modificati
            lottoScelto.setPrezzoVendita(nuovoPrezzo);

            appController.aggiornaPrezzoLotto(lottoScelto);
            Printer.print("[SUCCESS] Prezzo aggiornato correttamente.");

        } catch (NumberFormatException e) {
            Printer.perror("Formato numerico non valido.");
        } catch (GestioneException e) {
            Printer.perror("[ERRORE] " + e.getMessage());
        }
    }

    private void applicaScontiScadenza(Scanner scanner) {
        Printer.print("\n--- Analisi Lotti in Scadenza (entro 48h) ---");
        // Passiamo 2 giorni (48 ore) come da requisiti (FR-5)
        List<LottoBean> lottiInScadenza = appController.getLottiInScadenza(2);

        if (lottiInScadenza.isEmpty()) {
            Printer.print("Nessun lotto in scadenza sprovvisto di sconto.");
            return;
        }

        Printer.printf("%-5s %-15s %-15s %-15s %-15s\n", "NUM", "ID LOTTO", "PRODOTTO", "SCADENZA", "PREZZO ATTUALE");
        Printer.print("-------------------------------------------------------------------------");
        for (int i = 0; i < lottiInScadenza.size(); i++) {
            LottoBean l = lottiInScadenza.get(i);
            Printer.printf("%-5d %-15s %-15s %-15s %-15.2f EUR\n",
                    (i+1),
                    l.getIdLotto(),
                    l.getNomeProdotto(),
                    l.getDataScadenza().toString(),
                    l.getPrezzoVendita());
        }

        Printer.print("\nInserisci il NUM del lotto a cui applicare lo sconto (oppure 0 per annullare): ");
        try {
            int num = Integer.parseInt(scanner.nextLine().trim());
            if (num == 0) return;
            if (num < 1 || num > lottiInScadenza.size()) {
                Printer.perror("Numero non valido.");
                return;
            }
            
            LottoBean l = lottiInScadenza.get(num - 1);
            Printer.print("Inserisci il nuovo prezzo SCONTATO (EUR): ");
            double prezzoScontato = Double.parseDouble(scanner.nextLine());

            l.setScontoScadenzaAttivo(true);
            l.setPrezzoScontato(prezzoScontato);

            appController.aggiornaPrezzoLotto(l);
            Printer.print("[SUCCESS] Sconto applicato per il lotto " + l.getIdLotto());

        } catch (NumberFormatException e) {
            Printer.perror("Valore non numerico, sconto annullato.");
        } catch (GestioneException e) {
            Printer.perror("[ERRORE] " + e.getMessage());
        }
    }

    private void aggiornaCategoria(Scanner scanner) {
        List<ProdottoBean> catalogo = visualizzaCatalogo();
        if (catalogo.isEmpty()) return;

        Printer.print("\nInserisci il NUM del Prodotto da modificare (oppure 0 per annullare): ");
        try {
            int numProd = Integer.parseInt(scanner.nextLine().trim());
            if (numProd == 0) return;
            if (numProd < 1 || numProd > catalogo.size()) {
                Printer.perror("Numero non valido.");
                return;
            }
            String nomeProdotto = catalogo.get(numProd - 1).getNome();

            Printer.print("\nSeleziona la nuova categoria:");
            Printer.print("1. FRUTTA");
            Printer.print("2. VERDURA");
            Printer.print("Scelta (1-2, oppure 0 per annullare): ");
            
            int sceltaCat = Integer.parseInt(scanner.nextLine().trim());
            if (sceltaCat == 0) return;
            
            String nuovaCategoria;
            if (sceltaCat == 1) {
                nuovaCategoria = "FRUTTA";
            } else if (sceltaCat == 2) {
                nuovaCategoria = "VERDURA";
            } else {
                Printer.perror("Scelta non valida.");
                return;
            }

            ProdottoBean bean = new ProdottoBean(nomeProdotto, 0, 0, null, null);
            bean.setCategoria(nuovaCategoria);

            appController.aggiornaCategoriaProdotto(bean);
            Printer.print("[SUCCESS] Categoria aggiornata per " + nomeProdotto);
        } catch (NumberFormatException e) {
            Printer.perror("Formato numerico non valido.");
        } catch (GestioneException e) {
            Printer.perror("[ERRORE] " + e.getMessage());
        }
    }
}
