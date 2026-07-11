package it.ortogest.ortogestapp.graphiccontrollercli;

import java.util.Scanner;
import it.ortogest.ortogestapp.appcontroller.RegistrazioneAppController;
import it.ortogest.ortogestapp.beans.NuovoUtenteBean;
import it.ortogest.ortogestapp.utils.Printer;

public class RegistrazioneGraphicControllerCLI extends BaseGraphicControllerCLI {

    private final RegistrazioneAppController appController;

    public RegistrazioneGraphicControllerCLI() {
        this.appController = RegistrazioneAppController.getInstance();
    }

    @Override
    public void start(Scanner scanner) {
        Printer.print("--- Registrazione Nuovo Cliente ---");
        
        String nome = leggiStringaNonVuota(scanner, "Nome completo: ");
        String email = leggiStringaNonVuota(scanner, "Email: ");
        String password = leggiStringaNonVuota(scanner, "Password: ");
        String confermaPassword = leggiStringaNonVuota(scanner, "Conferma Password: ");
        
        if (!password.equals(confermaPassword)) {
            Printer.perror("Le password non coincidono. Registrazione annullata.");
            return;
        }
        
        NuovoUtenteBean nuovoUtente = new NuovoUtenteBean(nome, email, password, "Cliente");
        
        try {
            boolean successo = appController.registraNuovoCliente(nuovoUtente);
            if (successo) {
                Printer.print("Registrazione completata con successo! Ora puoi effettuare il login.");
            } else {
                Printer.perror("Impossibile completare la registrazione. (Email forse già in uso?)");
            }
        } catch (Exception e) {
            Printer.perror("Errore di sistema: " + e.getMessage());
        }
    }
}
