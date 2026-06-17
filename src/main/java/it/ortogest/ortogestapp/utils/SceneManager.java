package it.ortogest.ortogestapp.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * SceneManager è un Singleton che si occupa esclusivamente della gestione dello Stage primario
 * e del caricamento delle scene FXML.
 * Separando questa logica dai controller e dalla gestione dei dati (SessionManager),
 * rispettiamo il principio di Singola Responsabilità e riduciamo l'accoppiamento.
 * I controller chiederanno un cambio scena senza doversi preoccupare di *come* esso avvenga
 * o di dover configurare lo Stage in prima persona.
 */
@SuppressWarnings("java:S6548")
public class SceneManager {
    private static SceneManager instance;
    private Stage primaryWindow;

    // Costruttore privato
    private SceneManager() {}

    // Metodo per ottenere l'istanza Singleton
    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    // Inizializza lo Stage primario (chiamato una sola volta dall'AppStarter)
    public void initStage(Stage stage) {
        this.primaryWindow = stage;
        this.primaryWindow.setTitle("OrtoGest");
    }

    // Unico metodo astratto e generico per il cambio schermata
    public void cambiaScena(String fxmlPath) throws IOException {
        URL resource = getClass().getResource(fxmlPath);
        if (resource == null) {
            throw new IOException("Impossibile trovare il file FXML: " + fxmlPath);
        }
        Parent root = FXMLLoader.load(resource);
        if (primaryWindow.getScene() == null) {
            primaryWindow.setScene(new Scene(root));
        } else {
            primaryWindow.getScene().setRoot(root);
        }
        primaryWindow.show();
    }
}
