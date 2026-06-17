package it.ortogest.ortogestapp.graphiccontroller;

import it.ortogest.ortogestapp.appcontroller.GestioneMagazzinoAppController;
import it.ortogest.ortogestapp.beans.AnomaliaBean;
import it.ortogest.ortogestapp.utils.Printer;
import it.ortogest.ortogestapp.utils.CostantiGUI;
import it.ortogest.ortogestapp.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.io.IOException;

/**
 * Controller Grafico per la schermata di Segnalazione Anomalia.
 */
public class SegnalazioneAnomaliaGraphicController extends BaseGraphicController {

    @FXML
    private ComboBox<String> tipoAnomaliaComboBox;

    @FXML
    private TextField prodottoField;

    @FXML
    private TextField quantitaField;

    @FXML
    private TextArea noteArea;

    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {
        tipoAnomaliaComboBox.getItems().addAll("Mancante", "Danneggiata");
    }

    @FXML
    public void inoltraSegnalazioneAction() {
        statusLabel.setVisible(false);

        // 1. Validazione base dei dati inseriti dalla grafica
        String tipo = tipoAnomaliaComboBox.getValue();
        String prodotto = prodottoField.getText();
        String quantitaStr = quantitaField.getText();
        String note = noteArea.getText();

        if (tipo == null || prodotto == null || prodotto.trim().isEmpty() || quantitaStr == null
                || quantitaStr.trim().isEmpty()) {
            mostraStatus("Tutti i campi obbligatori devono essere compilati.", false);
            return;
        }

        double quantita;
        try {
            quantita = Double.parseDouble(quantitaStr);
            if (quantita <= 0)
                throw new NumberFormatException();
        } catch (NumberFormatException _) {
            mostraStatus("Inserire una quantitÃƒÆ’Ã‚Â  numerica valida (maggiore di 0).", false);
            return;
        }

        // 2. Creazione Bean e comunicazione con l'Application Controller
        AnomaliaBean anomaliaBean = new AnomaliaBean(tipo, prodotto, quantita, note);
        GestioneMagazzinoAppController appController = new GestioneMagazzinoAppController();

        String messaggioRisultato = appController.inoltraSegnalazione(anomaliaBean);

        // 3. Mostro successo e torno al Magazzino
        mostraStatus(messaggioRisultato, true);
        Printer.printf(messaggioRisultato);

        try {
            SceneManager.getInstance().cambiaScena(CostantiGUI.VIEW_MAGAZZINO);
        } catch (IOException e) {
            Printer.perror("Errore tornando al Magazzino: " + e.getMessage());
        }
    }

    @FXML
    public void annullaAction() {
        try {
            SceneManager.getInstance().cambiaScena(CostantiGUI.VIEW_MAGAZZINO);
        } catch (IOException e) {
            Printer.perror("Errore tornando al Magazzino: " + e.getMessage());
        }
    }

    @FXML
    public void indietroAction() {
        cambiaScenaSicuro(CostantiGUI.VIEW_MAGAZZINO, "Errore tornando al Magazzino:");
    }

    private void mostraStatus(String messaggio, boolean successo) {
        mostraStatusLabel(statusLabel, messaggio, successo);
    }
}
