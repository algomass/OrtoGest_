package it.ortogest.ortogestapp.graphiccontrollercli;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

import it.ortogest.ortogestapp.appcontroller.RegistraLottoAppController;
import it.ortogest.ortogestapp.beans.AnomaliaBean;
import it.ortogest.ortogestapp.beans.LottoBean;
import it.ortogest.ortogestapp.beans.ProdottoBean;
import it.ortogest.ortogestapp.exception.GestioneException;
import it.ortogest.ortogestapp.utils.Printer;
import it.ortogest.ortogestapp.utils.SessionManager;

public class MagazziniereGraphicControllerCLI extends BaseGraphicControllerCLI {

    private static final String MSG_INVIO_MANTENERE = ") [Invio per mantenere]: ";
    private static final String ERRORE_PREFIX = "[ERRORE] ";

    private final RegistraLottoAppController appController;
    private final DateTimeFormatter dateFormatter;

    public MagazziniereGraphicControllerCLI() {
        this.appController = new RegistraLottoAppController();
        this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }

    @Override
    public void start(Scanner scanner) {
        boolean exit = false;
        String userName = SessionManager.getInstance().getCurrentUser().getNome();

        while (!exit) {
            stampaMenu("DASHBOARD MAGAZZINIERE",
                    "Bentornato, " + userName,
                    "1. Visualizza Inventario",
                    "2. Registra Nuovo Lotto",
                    "3. Segnala Anomalia (Merce Mancante/Danneggiata)",
                    "0. Logout");
            String scelta = leggiStringaNonVuota(scanner, "Scelta: ");

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
                    eseguiLogout();
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

        int numProd = leggiInteroValido(scanner, "\nInserisci il NUM del Prodotto per visualizzarne i lotti (oppure 0 per tornare al menu): ", 0, inventario.size());
        if (numProd == 0) return;
        String nomeProdotto = inventario.get(numProd - 1).getNome();
        try {
            gestioneLottiProdotto(scanner, nomeProdotto);
        } catch (it.ortogest.ortogestapp.exception.ItemNotFoundException e) {
            Printer.perror(ERRORE_PREFIX + e.getMessage());
        } catch (GestioneException e) {
            Printer.perror("[ERRORE SISTEMA] " + e.getMessage());
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

        String azione = leggiStringaOpzionale(scanner, "\nVuoi modificare (M) o eliminare (E) un lotto? (oppure INVIO per annullare): ").toUpperCase();
        if (azione.isEmpty()) return;

        if (!azione.equals("M") && !azione.equals("E")) {
            Printer.perror("Scelta non valida.");
            return;
        }

        int numLotto = leggiInteroValido(scanner, "Inserisci il NUM del lotto: ", 1, lotti.size());
        
        LottoBean lottoDaModificare = lotti.get(numLotto - 1);
        try {
            eseguiAzioneSuLotto(scanner, lottoDaModificare, azione);
        } catch (NumberFormatException e) {
            it.ortogest.ortogestapp.exception.ValidationException ve = new it.ortogest.ortogestapp.exception.ValidationException("Hai inserito un formato numerico non valido durante la modifica.", e);
            Printer.perror(ERRORE_PREFIX + ve.getMessage());
        }
    }

    private void eseguiAzioneSuLotto(Scanner scanner, LottoBean lottoDaModificare, String azione) throws GestioneException {
        if (azione.equals("E")) {
            appController.eliminaLotto(lottoDaModificare.getIdLotto());
            Printer.print("[SUCCESS] Lotto eliminato.");
        } else if (azione.equals("M")) {
            Printer.print("Nuovo Fornitore (attuale: " + lottoDaModificare.getNomeFornitore() + MSG_INVIO_MANTENERE);
            String nuovoFornitore = scanner.nextLine().trim();
            if (!nuovoFornitore.isEmpty()) lottoDaModificare.setNomeFornitore(nuovoFornitore);
            
            Printer.print("Nuova Quantità (attuale: " + lottoDaModificare.getQuantitaKg() + MSG_INVIO_MANTENERE);
            String nuovaQuantita = scanner.nextLine().trim();
            if (!nuovaQuantita.isEmpty()) lottoDaModificare.setQuantitaKg(Double.parseDouble(nuovaQuantita));
            
            Printer.print("Nuovo Costo Acquisto (attuale: " + lottoDaModificare.getCostoAcquisto() + MSG_INVIO_MANTENERE);
            String nuovoCosto = scanner.nextLine().trim();
            if (!nuovoCosto.isEmpty()) lottoDaModificare.setCostoAcquisto(Double.parseDouble(nuovoCosto));
            
            appController.modificaLotto(lottoDaModificare);
            Printer.print("[SUCCESS] Lotto modificato.");
        }
    }

    private void registraLotto(Scanner scanner) {
        Printer.print("\n--- Registrazione Nuovo Lotto ---");
        try {
            String idLotto = leggiStringaNonVuota(scanner, "ID Lotto: ");
            String fornitore = leggiStringaNonVuota(scanner, "Nome Fornitore: ");
            String prodotto = leggiStringaNonVuota(scanner, "Nome Prodotto (es. Mele, Zucchine): ");
            double quantita = leggiDoubleValido(scanner, "Quantità (in Kg): ", 0.01);
            double costo = leggiDoubleValido(scanner, "Costo di Acquisto Totale (EUR): ", 0.0);

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

        } catch (it.ortogest.ortogestapp.exception.ValidationException | it.ortogest.ortogestapp.exception.DuplicateItemException | it.ortogest.ortogestapp.exception.ItemNotFoundException e) {
            Printer.perror(ERRORE_PREFIX + "Impossibile registrare il lotto: " + e.getMessage());
        } catch (GestioneException e) {
            Printer.perror("[ERRORE SISTEMA] " + e.getMessage());
        } catch (Exception e) {
            Printer.perror(ERRORE_PREFIX + e.getMessage());
        }
    }

    private void segnalaAnomalia(Scanner scanner) {
        Printer.print("\n--- Segnalazione Anomalia di Fornitura ---");
        String tipo = leggiStringaNonVuota(scanner, "Tipo di Anomalia (Mancante / Danneggiata): ");
        String email = leggiStringaNonVuota(scanner, "Email Fornitore: ");
        String prodotto = leggiStringaNonVuota(scanner, "Nome del Prodotto interessato: ");
        double quantita = leggiDoubleValido(scanner, "Quantità interessata (Kg): ", 0.01);
        String note = leggiStringaOpzionale(scanner, "Note aggiuntive: ");

        AnomaliaBean anomalia = new AnomaliaBean(tipo, prodotto, quantita, note, email);
        
        String risultato = appController.inoltraSegnalazione(anomalia);
        Printer.print("[ESITO] " + risultato);
    }

    private LocalDate leggiData(Scanner scanner, String messaggio) {
        while (true) {
            Printer.print(messaggio);
            String input = scanner.nextLine();
            try {
                return LocalDate.parse(input, dateFormatter);
            } catch (DateTimeParseException _) {
                Printer.perror(ERRORE_PREFIX + "Formato data non valido. Assicurati di usare il formato YYYY-MM-DD (es. 2024-05-20). Riprova.");
                // Continua il loop per far riprovare
            }
        }
    }
}
