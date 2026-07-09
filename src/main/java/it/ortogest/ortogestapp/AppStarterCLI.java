package it.ortogest.ortogestapp;

import java.util.Scanner;

import it.ortogest.ortogestapp.graphiccontrollercli.LoginGraphicControllerCLI;
import it.ortogest.ortogestapp.utils.Printer;


public class AppStarterCLI {

    public static void main() {
        Printer.print("Avvio OrtoGest CLI in corso...");

        
        try (Scanner scanner = new Scanner(System.in)) {

            
            LoginGraphicControllerCLI loginController = new LoginGraphicControllerCLI();
            loginController.start(scanner);

        } catch (Exception e) {
            Printer.perror("Errore fatale: " + e.getMessage());
        }

        Printer.print("Arrivederci!");
    }
}
