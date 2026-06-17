package it.ortogest.ortogestapp;

import java.util.Scanner;

import it.ortogest.ortogestapp.graphiccontrollercli.LoginGraphicControllerCLI;
import it.ortogest.ortogestapp.utils.Printer;

/**
 * Entry point per l'applicazione versione Command Line Interface (CLI).
 */
public class AppStarterCLI {

    public static void main() {
        Printer.print("Avvio OrtoGest CLI in corso...");

        // Istanziamo un unico Scanner per tutta l'esecuzione dell'app
        try (Scanner scanner = new Scanner(System.in)) {

            // Il primo controller ad essere chiamato è sempre il Login
            LoginGraphicControllerCLI loginController = new LoginGraphicControllerCLI();
            loginController.start(scanner);

        } catch (Exception e) {
            Printer.perror("Errore fatale: " + e.getMessage());
        }

        Printer.print("Arrivederci!");
    }
}
