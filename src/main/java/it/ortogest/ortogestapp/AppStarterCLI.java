package it.ortogest.ortogestapp;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

import it.ortogest.ortogestapp.utils.*;

public class AppStarterCLI extends Application {

    @Override
    public void start(Stage stage)  {
        try {
            // Aggiunta consigliata: imposta il titolo della finestra dell'app
            stage.setTitle("OrtoGest");

            // 3. Cambia il percorso per puntare al tuo file FXML iniziale
            SceneManager sceneManager = SceneManager.getInstance();
            sceneManager.initStage(stage);
            sceneManager.cambiaScena(CostantiGUI.VIEW_LOGIN);

            stage.show();
        } catch (IOException e) {
            // 4. Assicurati di avere la classe Printer, altrimenti usa System.err.println
            Printer.perror(e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}