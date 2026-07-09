package it.ortogest.ortogestapp.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;


@SuppressWarnings("java:S6548")
public class SceneManager {
    private static SceneManager instance;
    private Stage primaryWindow;

    
    private SceneManager() {}

    
    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    
    public void initStage(Stage stage) {
        this.primaryWindow = stage;
        this.primaryWindow.setTitle("OrtoGest");
    }

    
    public void cambiaScena(String fxmlPath) throws it.ortogest.ortogestapp.exception.ViewException {
        URL resource = getClass().getResource(fxmlPath);
        if (resource == null) {
            throw new it.ortogest.ortogestapp.exception.ViewException("Impossibile trovare il file FXML: " + fxmlPath);
        }
        try {
            Parent root = FXMLLoader.load(resource);
            if (primaryWindow.getScene() == null) {
                primaryWindow.setScene(new Scene(root));
            } else {
                primaryWindow.getScene().setRoot(root);
            }
            primaryWindow.show();
        } catch(IOException e) {
            throw new it.ortogest.ortogestapp.exception.ViewException("Errore nel caricamento del file FXML: " + fxmlPath, e);
        }
    }
}
