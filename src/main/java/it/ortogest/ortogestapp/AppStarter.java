package it.ortogest.ortogestapp;

import javafx.application.Application;
import javafx.stage.Stage;

import it.ortogest.ortogestapp.utils.CostantiGUI;
import it.ortogest.ortogestapp.utils.Printer;
import it.ortogest.ortogestapp.utils.SceneManager;


public class AppStarter extends Application {

    @Override
    public void start(Stage stage) {
        try {
            
            SceneManager sceneManager = SceneManager.getInstance();
            sceneManager.initStage(stage);

            
            
            
            sceneManager.cambiaScena(CostantiGUI.VIEW_LOGIN);

        } catch (it.ortogest.ortogestapp.exception.ViewException e) {
            
            Printer.perror("Errore durante l'avvio dell'applicazione: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
