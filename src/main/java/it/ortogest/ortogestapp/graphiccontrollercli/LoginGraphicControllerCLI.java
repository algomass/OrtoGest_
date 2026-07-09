package it.ortogest.ortogestapp.graphiccontrollercli;

import java.util.Scanner;

import it.ortogest.ortogestapp.appcontroller.LoginAppController;
import it.ortogest.ortogestapp.beans.CredenzialiBean;
import it.ortogest.ortogestapp.beans.UtenteBean;
import it.ortogest.ortogestapp.exception.LoginFallitoException;
import it.ortogest.ortogestapp.utils.Printer;
import it.ortogest.ortogestapp.utils.SessionManager;


public class LoginGraphicControllerCLI extends BaseGraphicControllerCLI {

    private final LoginAppController loginAppController;

    public LoginGraphicControllerCLI() {
        this.loginAppController = new LoginAppController();
    }

    @Override
    public void start(Scanner scanner) {
        boolean exit = false;

        while (!exit) {
            stampaMenu("BENVENUTO IN ORTOGEST (CLI)",
                    "1. Effettua il Login",
                    "0. Esci dall'applicazione");
            String scelta = leggiStringaNonVuota(scanner, "Scelta: ");

            switch (scelta) {
                case "1":
                    eseguiLogin(scanner);
                    
                    UtenteBean utenteLoggato = SessionManager.getInstance().getCurrentUser();
                    if (utenteLoggato != null) {
                        navigaDopoLogin(utenteLoggato, scanner);
                        
                        
                    }
                    break;
                case "0":
                    Printer.print("Uscita in corso...");
                    exit = true;
                    break;
                default:
                    Printer.perror("Scelta non valida, riprova.");
            }
        }
    }

    private void eseguiLogin(Scanner scanner) {
        Printer.print("--- Inserimento Credenziali ---");
        String email = leggiStringaNonVuota(scanner, "Email: ");
        String password = leggiStringaNonVuota(scanner, "Password: ");

        CredenzialiBean credenziali = new CredenzialiBean(email, password);

        try {
            
            UtenteBean utenteModel = loginAppController.login(credenziali);
            
            
            SessionManager.getInstance().login(utenteModel);
            
            Printer.print("Login effettuato con successo! Benvenuto " + utenteModel.getNome());

        } catch (LoginFallitoException e) {
            Printer.perror("Errore di Autenticazione: " + e.getMessage());
        }
    }

    private void navigaDopoLogin(UtenteBean utenteLoggato, Scanner scanner) {
        String ruolo = utenteLoggato.getRuolo();
        GraphicControllerCLI nextView = null;

        Printer.print("Reindirizzamento verso l'area: " + ruolo + "...");

        switch (ruolo) {
            case "Magazziniere":
                nextView = new MagazziniereGraphicControllerCLI();
                break;
            case "Responsabile":
                nextView = new ResponsabileGraphicControllerCLI();
                break;
            case "Operatore":
                nextView = new OperatoreGraphicControllerCLI();
                break;
            case "Cliente":
                nextView = new ClienteGraphicControllerCLI();
                break;
            default:
                Printer.perror("Ruolo non riconosciuto: " + ruolo);
        }

        if (nextView != null) {
            nextView.start(scanner);
        } else {
            
            SessionManager.getInstance().logout();
        }
    }
}
