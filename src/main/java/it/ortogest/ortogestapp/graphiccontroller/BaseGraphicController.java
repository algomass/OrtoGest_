package it.ortogest.ortogestapp.graphiccontroller;

import it.ortogest.ortogestapp.utils.Printer;
import it.ortogest.ortogestapp.utils.CostantiGUI;
import it.ortogest.ortogestapp.utils.SceneManager;
import it.ortogest.ortogestapp.utils.SessionManager;
import javafx.fxml.FXML;

import java.io.IOException;

/**
 * Controller Grafico Base (Boundary).
 * Contiene i metodi e i comportamenti comuni a tutte le schermate, 
 * come ad esempio l'azione di logout (Esci).
 */
public abstract class BaseGraphicController {

    @FXML
    public void logoutAction() {
        // 1. Pulisco la sessione globale rimuovendo l'utente corrente
        SessionManager.getInstance().logout();
        Printer.printf("Logout effettuato con successo. Ritorno alla schermata iniziale.");

        // 2. Chiedo allo SceneManager di riportarmi alla view di Login
        try {
            SceneManager.getInstance().cambiaScena(CostantiGUI.VIEW_LOGIN);
        } catch (IOException e) {
            Printer.perror("Errore critico durante il logout: impossibile ricaricare Login.fxml. " + e.getMessage());
        }
    }
}
