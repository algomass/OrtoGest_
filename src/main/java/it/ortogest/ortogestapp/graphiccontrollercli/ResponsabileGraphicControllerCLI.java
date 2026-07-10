package it.ortogest.ortogestapp.graphiccontrollercli;

import java.util.List;
import java.util.Scanner;

import it.ortogest.ortogestapp.appcontroller.GestisciCatalogoAppController;
import it.ortogest.ortogestapp.beans.LottoBean;
import it.ortogest.ortogestapp.beans.ProdottoBean;
import it.ortogest.ortogestapp.exception.GestioneException;
import it.ortogest.ortogestapp.utils.Printer;
import it.ortogest.ortogestapp.utils.SessionManager;

public class ResponsabileGraphicControllerCLI extends BaseGraphicControllerCLI {

    private static final String ROW_FORMAT = "%-5s %-15s %-15s %-15s %-15s\n";
    private static final String MSG_ERRORE = "[ERRORE] ";
    private static final String HEADER_ID_LOTTO = "ID LOTTO";
    private static final String HEADER_PRODOTTO = "PRODOTTO";

    private final GestisciCatalogoAppController appController;

    public ResponsabileGraphicControllerCLI() {
        this.appController = new GestisciCatalogoAppController();
    }

    @Override
    public void start(Scanner scanner) {
        boolean exit = false;
        String userName = SessionManager.getInstance().getCurrentUser().getNome();

        while (!exit) {
            stampaMenu("DASHBOARD RESPONSABILE",
                    "Bentornato, " + userName,
                    "1. Visualizza Catalogo Completo",
                    "2. Imposta/Modifica Prezzo di Vendita Lotto",
                    "3. Monitora Scadenze e Applica Sconti",
                    "4. Aggiorna Categoria Prodotto",
                    "5. Visualizza Lotti da Prezzare",
                    "0. Logout");
            String scelta = leggiStringaNonVuota(scanner, "Scelta: ");

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
                case "5":
                    visualizzaDaPrezzare(scanner);
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

    private List<ProdottoBean> visualizzaCatalogo() {
        Printer.print("\n--- Catalogo Completo ---");
        List<ProdottoBean> catalogo = appController.getTuttiIProdotti();

        if (catalogo.isEmpty()) {
            Printer.print("Il catalogo è vuoto.");
            return catalogo;
        }

        Printer.printf(ROW_FORMAT, "NUM", HEADER_PRODOTTO, "CATEGORIA", "GIACENZA TOT", "COSTO MEDIO");
        Printer.print("-----------------------------------------------------------------");
        for (int i = 0; i < catalogo.size(); i++) {
            ProdottoBean p = catalogo.get(i);
            Printer.printf("%-5d %-15s %-15s %-15.2f %-15.2f EUR\n",
                    (i + 1),
                    p.getNome(),
                    p.getCategoria(),
                    p.getGiacenza(),
                    p.getPrezzoAcquistoMedio());
        }
        return catalogo;
    }

    private void modificaPrezzoLotto(Scanner scanner) {
        List<ProdottoBean> catalogo = visualizzaCatalogo();
        if (catalogo.isEmpty())
            return;

        int numProd = leggiInteroValido(scanner,
                "\nInserisci il NUM del Prodotto per visualizzarne i lotti (oppure 0 per annullare): ", 0,
                catalogo.size());
        if (numProd == 0)
            return;
        String nomeProdotto = catalogo.get(numProd - 1).getNome();

        try {

            List<LottoBean> lotti = appController.getLottiPerProdotto(nomeProdotto);

            if (lotti.isEmpty()) {
                Printer.perror("Nessun lotto trovato per questo prodotto.");
                return;
            }

            Printer.printf(ROW_FORMAT, "NUM", HEADER_ID_LOTTO, "COSTO ACQ.", "PREZZO VENDITA", "SCONTO ATTIVO");
            Printer.print("-------------------------------------------------------------------");
            for (int i = 0; i < lotti.size(); i++) {
                LottoBean l = lotti.get(i);
                Printer.printf("%-5d %-15s %-15.2f EUR %-15.2f EUR %-15s\n",
                        (i + 1),
                        l.getIdLotto(),
                        l.getCostoAcquisto(),
                        l.getPrezzoVendita(),
                        l.isScontoScadenzaAttivo() ? "SI (" + l.getPrezzoScontato() + " EUR)" : "NO");
            }

            int numLotto = leggiInteroValido(scanner,
                    "\nInserisci il NUM del lotto da modificare (oppure 0 per annullare): ", 0, lotti.size());
            if (numLotto == 0)
                return;

            LottoBean lottoScelto = lotti.get(numLotto - 1);

            Printer.print("Attuale costo di acquisto: " + lottoScelto.getCostoAcquisto() + " EUR");
            double nuovoPrezzo = leggiDoubleValido(scanner, "Nuovo Prezzo di Vendita al pubblico (EUR): ", 0.0);

            
            lottoScelto.setPrezzoVendita(nuovoPrezzo);

            appController.aggiornaPrezzoLotto(lottoScelto);
            Printer.print("[SUCCESS] Prezzo aggiornato correttamente.");

        } catch (it.ortogest.ortogestapp.exception.ItemNotFoundException e) {
            Printer.perror(MSG_ERRORE + e.getMessage());
        } catch (GestioneException e) {
            Printer.perror(MSG_ERRORE + e.getMessage());
        }
    }

    private void applicaScontiScadenza(Scanner scanner) {
        Printer.print("\n--- Analisi Lotti in Scadenza (entro 48h) ---");
        
        List<LottoBean> lottiInScadenza = appController.getLottiInScadenza(2);

        if (lottiInScadenza.isEmpty()) {
            Printer.print("Nessun lotto in scadenza sprovvisto di sconto.");
            return;
        }

        Printer.printf(ROW_FORMAT, "NUM", HEADER_ID_LOTTO, HEADER_PRODOTTO, "SCADENZA", "PREZZO ATTUALE");
        Printer.print("-------------------------------------------------------------------------");
        for (int i = 0; i < lottiInScadenza.size(); i++) {
            LottoBean l = lottiInScadenza.get(i);
            Printer.printf("%-5d %-15s %-15s %-15s %-15.2f EUR\n",
                    (i + 1),
                    l.getIdLotto(),
                    l.getNomeProdotto(),
                    l.getDataScadenza().toString(),
                    l.getPrezzoVendita());
        }

        int num = leggiInteroValido(scanner,
                "\nInserisci il NUM del lotto a cui applicare lo sconto (oppure 0 per annullare): ", 0,
                lottiInScadenza.size());
        if (num == 0)
            return;

        LottoBean l = lottiInScadenza.get(num - 1);
        double prezzoScontato = leggiDoubleValido(scanner, "Inserisci il nuovo prezzo SCONTATO (EUR): ", 0.0);

        try {

            l.setScontoScadenzaAttivo(true);
            l.setPrezzoScontato(prezzoScontato);

            appController.aggiornaPrezzoLotto(l);
            Printer.print("[SUCCESS] Sconto applicato per il lotto " + l.getIdLotto());

        } catch (it.ortogest.ortogestapp.exception.ItemNotFoundException e) {
            Printer.perror(MSG_ERRORE + e.getMessage());
        } catch (GestioneException e) {
            Printer.perror(MSG_ERRORE + e.getMessage());
        }
    }

    private void aggiornaCategoria(Scanner scanner) {
        List<ProdottoBean> catalogo = visualizzaCatalogo();
        if (catalogo.isEmpty())
            return;

        int numProd = leggiInteroValido(scanner,
                "\nInserisci il NUM del Prodotto da modificare (oppure 0 per annullare): ", 0, catalogo.size());
        if (numProd == 0)
            return;
        String nomeProdotto = catalogo.get(numProd - 1).getNome();

        Printer.print("\nSeleziona la nuova categoria:");
        Printer.print("1. FRUTTA");
        Printer.print("2. VERDURA");

        int sceltaCat = leggiInteroValido(scanner, "Scelta (1-2, oppure 0 per annullare): ", 0, 2);
        if (sceltaCat == 0)
            return;

        String nuovaCategoria = switch (sceltaCat) {
            case 1 -> "FRUTTA";
            case 2 -> "VERDURA";
            default -> null;
        };
        if (nuovaCategoria == null) {
            Printer.perror("Scelta non valida.");
            return;
        }

        ProdottoBean bean = new ProdottoBean(nomeProdotto, 0, 0, null);
        bean.setCategoria(nuovaCategoria);

        try {
            appController.aggiornaCategoriaProdotto(bean);
            Printer.print("[SUCCESS] Categoria aggiornata per " + nomeProdotto);
        } catch (it.ortogest.ortogestapp.exception.ItemNotFoundException e) {
            Printer.perror(MSG_ERRORE + e.getMessage());
        } catch (GestioneException e) {
            Printer.perror(MSG_ERRORE + e.getMessage());
        }
    }

    private void visualizzaDaPrezzare(Scanner scanner) {
        Printer.print("\n--- Lotti da Prezzare ---");
        List<LottoBean> lottiDaPrezzare = appController.getLottiDaPrezzare();

        if (lottiDaPrezzare.isEmpty()) {
            Printer.print("Nessun lotto da prezzare.");
            return;
        }

        Printer.printf(ROW_FORMAT, "NUM", HEADER_ID_LOTTO, HEADER_PRODOTTO, "COSTO ACQ.", "PREZZO VENDITA");
        Printer.print("-------------------------------------------------------------------------");
        for (int i = 0; i < lottiDaPrezzare.size(); i++) {
            LottoBean l = lottiDaPrezzare.get(i);
            Printer.printf("%-5d %-15s %-15s %-15.2f EUR %-15.2f EUR\n",
                    (i + 1),
                    l.getIdLotto(),
                    l.getNomeProdotto(),
                    l.getCostoAcquisto(),
                    l.getPrezzoVendita());
        }

        Printer.print("\nPremi INVIO per tornare al menu...");
        scanner.nextLine();
    }
}
