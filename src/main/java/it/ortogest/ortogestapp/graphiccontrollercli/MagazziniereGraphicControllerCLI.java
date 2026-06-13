package it.ortogest.ortogestapp.graphiccontrollercli;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

import it.ortogest.ortogestapp.appcontroller.GestioneMagazzinoAppController;
import it.ortogest.ortogestapp.beans.AnomaliaBean;
import it.ortogest.ortogestapp.beans.LottoBean;
import it.ortogest.ortogestapp.beans.ProdottoBean;
import it.ortogest.ortogestapp.exception.GestioneException;
import it.ortogest.ortogestapp.utils.Printer;
import it.ortogest.ortogestapp.utils.SessionManager;

public class MagazziniereGraphicControllerCLI implements GraphicControllerCLI {

    private final GestioneMagazzinoAppController appController;
    private final DateTimeFormatter dateFormatter;

    public MagazziniereGraphicControllerCLI() {
        this.appController = new GestioneMagazzinoAppController();
        this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }

    @Override
    public void start(Scanner scanner) {
        boolean exit = false;
        String userName = SessionManager.getInstance().getCurrentUser().getNome();

        while (!exit) {
            Printer.print("\n=================================");
            Printer.print("     DASHBOARD MAGAZZINIERE      ");
            Printer.print("     Bentornato, " + userName);
            Printer.print("=================================");
            Printer.print("1. Visualizza Inventario");
            Printer.print("2. Registra Nuovo Lotto");
            Printer.print("3. Segnala Anomalia (Merce Mancante/Danneggiata)");
            Printer.print("0. Logout");
            Printer.print("Scelta: ");

            String scelta = scanner.nextLine();

            switch (scelta) {
                case "1":
                    visualizzaInventario(scanner);
                    break;
                case "2":
                    registraLotto(scanner);
                    break;
                case "3":
                    segnalaAnomalia(scanner);
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

    private void visualizzaInventario(Scanner scanner) {
        Printer.print("\n--- Inventario Attuale ---");
        List<ProdottoBean> inventario = appController.getInventario();

        if (inventario.isEmpty()) {
            Printer.print("Il magazzino è vuoto.");
            return;
        }

        Printer.printf("%-5s %-20s %-15s %-15s\n", "NUM", "PRODOTTO", "GIACENZA (Kg)", "PREZZO VENDITA");
        Printer.print("---------------------------------------------------------");
        for (int i = 0; i < inventario.size(); i++) {
            ProdottoBean p = inventario.get(i);
            Printer.printf("%-5d %-20s %-15.2f EUR %-14.2f\n", 
                (i+1),
                p.getNome(), 
                p.getGiacenza(), 
                p.getPrezzoAttuale());
        }

        Printer.print("\nInserisci il NUM del Prodotto per visualizzarne i lotti (oppure 0 per tornare al menu): ");
        try {
            int numProd = Integer.parseInt(scanner.nextLine().trim());
            if (numProd == 0) return;
            if (numProd < 1 || numProd > inventario.size()) {
                Printer.perror("[ERRORE] Numero non valido.");
                return;
            }
            String nomeProdotto = inventario.get(numProd - 1).getNome();
            gestioneLottiProdotto(scanner, nomeProdotto);
            
        } catch (NumberFormatException _) {
            Printer.perror("[ERRORE] Formato numerico non valido.");
        } catch (GestioneException e) {
            Printer.perror("[ERRORE] " + e.getMessage());
        }
    }

    private void gestioneLottiProdotto(Scanner scanner, String nomeProdotto) throws GestioneException {
        List<LottoBean> lotti = appController.getLottiPerProdotto(nomeProdotto);
        
        if (lotti.isEmpty()) {
            Printer.print("Nessun lotto trovato per " + nomeProdotto);
            return;
        }

        Printer.printf("\n--- Lotti per %s ---\n", nomeProdotto);
        Printer.printf("%-5s %-15s %-15s %-15s %-15s\n", "NUM", "ID LOTTO", "SCADENZA", "GIACENZA", "FORNITORE");
        Printer.print("-------------------------------------------------------------------------");
        for (int i = 0; i < lotti.size(); i++) {
            LottoBean l = lotti.get(i);
            Printer.printf("%-5d %-15s %-15s %-15.2f %-15s\n",
                    (i+1),
                    l.getIdLotto(),
                    l.getDataScadenza().toString(),
                    l.getQuantitaKg(),
                    l.getNomeFornitore());
        }

        Printer.print("\nVuoi modificare (M) o eliminare (E) un lotto? (oppure INVIO per annullare): ");
        String azione = scanner.nextLine().trim().toUpperCase();
        if (azione.isEmpty()) return;

        if (!azione.equals("M") && !azione.equals("E")) {
            Printer.perror("Scelta non valida.");
            return;
        }

        Printer.print("Inserisci il NUM del lotto: ");
        int numLotto = Integer.parseInt(scanner.nextLine().trim());
        if (numLotto < 1 || numLotto > lotti.size()) {
            Printer.perror("Numero non valido.");
            return;
        }
        
        LottoBean lottoDaModificare = lotti.get(numLotto - 1);
        eseguiAzioneSuLotto(scanner, lottoDaModificare, azione);
    }

    private void eseguiAzioneSuLotto(Scanner scanner, LottoBean lottoDaModificare, String azione) throws GestioneException {
        if (azione.equals("E")) {
            appController.eliminaLotto(lottoDaModificare.getIdLotto());
            Printer.print("[SUCCESS] Lotto eliminato.");
        } else if (azione.equals("M")) {
            Printer.print("Nuovo Fornitore (attuale: " + lottoDaModificare.getNomeFornitore() + ") [Invio per mantenere]: ");
            String nuovoFornitore = scanner.nextLine().trim();
            if (!nuovoFornitore.isEmpty()) lottoDaModificare.setNomeFornitore(nuovoFornitore);
            
            Printer.print("Nuova Quantità (attuale: " + lottoDaModificare.getQuantitaKg() + ") [Invio per mantenere]: ");
            String nuovaQuantita = scanner.nextLine().trim();
            if (!nuovaQuantita.isEmpty()) lottoDaModificare.setQuantitaKg(Double.parseDouble(nuovaQuantita));
            
            Printer.print("Nuovo Costo Acquisto (attuale: " + lottoDaModificare.getCostoAcquisto() + ") [Invio per mantenere]: ");
            String nuovoCosto = scanner.nextLine().trim();
            if (!nuovoCosto.isEmpty()) lottoDaModificare.setCostoAcquisto(Double.parseDouble(nuovoCosto));
            
            appController.modificaLotto(lottoDaModificare);
            Printer.print("[SUCCESS] Lotto modificato.");
        }
    }

    private void registraLotto(Scanner scanner) {
        Printer.print("\n--- Registrazione Nuovo Lotto ---");
        try {
            Printer.print("ID Lotto: ");
            String idLotto = scanner.nextLine();

            Printer.print("Nome Fornitore: ");
            String fornitore = scanner.nextLine();

            Printer.print("Nome Prodotto (es. Mele, Zucchine): ");
            String prodotto = scanner.nextLine();

            Printer.print("Quantità (in Kg): ");
            double quantita = Double.parseDouble(scanner.nextLine());

            Printer.print("Costo di Acquisto Totale (â‚¬): ");
            double costo = Double.parseDouble(scanner.nextLine());

            LocalDate dataArrivo = leggiData(scanner, "Data di Arrivo (YYYY-MM-DD): ");
            LocalDate dataScadenza = leggiData(scanner, "Data di Scadenza (YYYY-MM-DD): ");

            LottoBean lottoBean = LottoBean.builder()
                    .idLotto(idLotto)
                    .nomeFornitore(fornitore)
                    .nomeProdotto(prodotto)
                    .quantitaKg(quantita)
                    .costoAcquisto(costo)
                    .dataArrivo(dataArrivo)
                    .dataScadenza(dataScadenza)
                    .build();

            appController.registraLotto(lottoBean);
            Printer.print("[SUCCESS] Lotto registrato correttamente!");

        } catch (NumberFormatException _) {
            Printer.perror("[ERRORE] Formato numerico non valido per Quantità o Costo.");
        } catch (GestioneException e) {
            Printer.perror("[ERRORE] Impossibile registrare il lotto: " + e.getMessage());
        } catch (Exception e) {
            Printer.perror("[ERRORE] " + e.getMessage());
        }
    }

    private void segnalaAnomalia(Scanner scanner) {
        Printer.print("\n--- Segnalazione Anomalia di Fornitura ---");
        try {
            Printer.print("Tipo di Anomalia (Mancante / Danneggiata): ");
            String tipo = scanner.nextLine();

            Printer.print("Nome del Prodotto interessato: ");
            String prodotto = scanner.nextLine();

            Printer.print("Quantità interessata (Kg): ");
            double quantita = Double.parseDouble(scanner.nextLine());

            Printer.print("Note aggiuntive: ");
            String note = scanner.nextLine();

            AnomaliaBean anomalia = new AnomaliaBean(tipo, prodotto, quantita, note);
            
            String risultato = appController.inoltraSegnalazione(anomalia);
            Printer.print("[ESITO] " + risultato);

        } catch (NumberFormatException _) {
            Printer.perror("[ERRORE] Formato numerico non valido per la Quantità.");
        }
    }

    private LocalDate leggiData(Scanner scanner, String messaggio) {
        while (true) {
            Printer.print(messaggio);
            String input = scanner.nextLine();
            try {
                return LocalDate.parse(input, dateFormatter);
            } catch (DateTimeParseException e) {
                Printer.perror("[ERRORE] Formato data non valido. Assicurati di usare il formato YYYY-MM-DD (es. 2024-05-20). Riprova.");
                // Continua il loop per far riprovare
            }
        }
    }
}
