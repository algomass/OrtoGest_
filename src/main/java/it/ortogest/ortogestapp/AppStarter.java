package it.ortogest.ortogestapp;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

import it.ortogest.ortogestapp.utils.CostantiGUI;
import it.ortogest.ortogestapp.utils.Printer;
import it.ortogest.ortogestapp.utils.SceneManager;

/**
 * AppStarter è il punto di ingresso dell'applicazione JavaFX.
 * Il suo unico scopo è avviare l'infrastruttura di base, configurare lo
 * SceneManager
 * e delegare a quest'ultimo il caricamento della prima schermata.
 * Non contiene alcuna logica di business o gestione di sessione.
 */
public class AppStarter extends Application {

    @Override
    public void start(Stage stage) {
        try {
            // 1. Ottengo l'istanza del gestore delle scene e lo inizializzo
            SceneManager sceneManager = SceneManager.getInstance();
            sceneManager.initStage(stage);

            // 2. Carico la schermata iniziale di login.
            // N.B: Uso il path in CostantiGUI perché Maven posiziona le risorse
            // direttamente nella root "target/classes/GUI" durante la compilazione.
            sceneManager.cambiaScena(CostantiGUI.VIEW_LOGIN);

        } catch (IOException e) {
            // Gestione basilare degli errori di caricamento
            Printer.perror("Errore durante l'avvio dell'applicazione: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
