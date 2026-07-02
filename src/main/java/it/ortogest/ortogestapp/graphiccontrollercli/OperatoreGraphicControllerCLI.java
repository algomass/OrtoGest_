package it.ortogest.ortogestapp.graphiccontrollercli;

import java.util.List;
import java.util.Scanner;

import it.ortogest.ortogestapp.appcontroller.CreaOrdineAppController;
import it.ortogest.ortogestapp.beans.OrdineBean;
import it.ortogest.ortogestapp.utils.Printer;
import it.ortogest.ortogestapp.utils.SessionManager;

public class OperatoreGraphicControllerCLI extends BaseGraphicControllerCLI {

    private final CreaOrdineAppController appController;

    public OperatoreGraphicControllerCLI() {
        this.appController = new CreaOrdineAppController();
    }

    @Override
    public void start(Scanner scanner) {
        boolean exit = false;
        String userName = SessionManager.getInstance().getCurrentUser().getNome();

        while (!exit) {
            stampaMenu("CASSA / OPERATORE",
                    "Turno di: " + userName,
                    "1. Visualizza Ordini in attesa di ritiro",
                    "2. Evadi Ordine ed Emetti Scontrino",
                    "0. Logout");
            String scelta = leggiStringaNonVuota(scanner, "Scelta: ");

            switch (scelta) {
                case "1":
                    visualizzaOrdiniPronti();
                    break;
                case "2":
                    evadiOrdine(scanner);
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

    private void visualizzaOrdiniPronti() {
        Printer.print("\n--- Ordini Pronti per il Ritiro ---");
        List<OrdineBean> pronti = getOrdiniPronti();

        if (pronti.isEmpty()) {
            Printer.print("Nessun ordine in attesa di ritiro.");
            return;
        }

        Printer.printf("%-5s %-15s %-30s %-15s\n", "NUM", "ID ORDINE", "CLIENTE (EMAIL)", "TOTALE");
        Printer.print("---------------------------------------------------------------");
        for (int i = 0; i < pronti.size(); i++) {
            OrdineBean o = pronti.get(i);
            Printer.printf("%-5d %-15s %-30s %-15.2f EUR\n", 
                (i+1),
                o.getIdOrdine(), 
                o.getEmailCliente(), 
                o.getTotale());
        }
    }

    private void evadiOrdine(Scanner scanner) {
        visualizzaOrdiniPronti();
        
        List<OrdineBean> pronti = getOrdiniPronti();
        if (pronti.isEmpty()) return;

        int numOrdine = leggiInteroValido(scanner, "\nInserisci il NUM dell'ordine da evadere (oppure 0 per annullare): ", 0, pronti.size());
        if (numOrdine == 0) return;
        
        OrdineBean ordineDaEvadere = pronti.get(numOrdine - 1);

        Printer.print("\n--- Riepilogo Ordine #" + ordineDaEvadere.getIdOrdine() + " ---");
        Printer.print("Cliente: " + ordineDaEvadere.getEmailCliente());
        Printer.print("Prodotti: " + ordineDaEvadere.getRiepilogoProdotti());
        Printer.printf("TOTALE DA INCASSARE: %.2f EUR\n", ordineDaEvadere.getTotale());
        
        String conferma = leggiStringaNonVuota(scanner, "Confermi l'avvenuto pagamento? (S/N): ").toUpperCase();
        if ("S".equals(conferma)) {
            confermaEmettiScontrino(ordineDaEvadere);
        } else {
            Printer.print("Operazione annullata.");
        }
    }

    private void confermaEmettiScontrino(OrdineBean ordineDaEvadere) {
        try {
            appController.registraVendita(ordineDaEvadere.getIdOrdine());
            Printer.print("\n**************************************");
            Printer.print("         SCONTRINO EMESSO             ");
            Printer.print(" Ordine: " + ordineDaEvadere.getIdOrdine());
            Printer.printf(" Pagato: %.2f EUR\n", ordineDaEvadere.getTotale());
            Printer.print("**************************************");
            Printer.print("[SUCCESS] Ordine completato e passato allo stato 'Ritirato'.");
        } catch (it.ortogest.ortogestapp.exception.InvalidStateException | it.ortogest.ortogestapp.exception.ItemNotFoundException e) {
            Printer.perror("[ERRORE] Impossibile aggiornare lo stato dell'ordine: " + e.getMessage());
        } catch (it.ortogest.ortogestapp.exception.GestioneException e) {
            Printer.perror("[ERRORE DI SISTEMA] " + e.getMessage());
        }
    }

    private List<OrdineBean> getOrdiniPronti() {
        return appController.getOrdiniProntiPerRitiro();
    }
}
