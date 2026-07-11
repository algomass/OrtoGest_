package it.ortogest.ortogestapp.graphiccontroller;

import it.ortogest.ortogestapp.appcontroller.RegistraLottoAppController;
import it.ortogest.ortogestapp.beans.AnomaliaBean;
import it.ortogest.ortogestapp.utils.Printer;
import it.ortogest.ortogestapp.utils.CostantiGUI;
import it.ortogest.ortogestapp.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;


public class SegnalazioneAnomaliaGraphicController extends BaseGraphicController {

    @FXML
    private ComboBox<String> tipoAnomaliaComboBox;

    @FXML
    private TextField emailFornitoreField;

    @FXML
    private TextField prodottoField;

    @FXML
    private TextField lottoField;

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

        
        String tipo = tipoAnomaliaComboBox.getValue();
        String email = emailFornitoreField.getText();
        String prodotto = prodottoField.getText();
        String lotto = lottoField.getText();
        String quantitaStr = quantitaField.getText();
        String note = noteArea.getText();

        if (tipo == null || email == null || email.trim().isEmpty() || prodotto == null || prodotto.trim().isEmpty()
                || lotto == null || lotto.trim().isEmpty()
                || quantitaStr == null
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
            mostraStatus("Inserire una quantità numerica valida (maggiore di 0).", false);
            return;
        }

        
        AnomaliaBean anomaliaBean = new AnomaliaBean(tipo, prodotto, lotto, quantita, note, email);
        RegistraLottoAppController appController = RegistraLottoAppController.getInstance();

        String messaggioRisultato = appController.inoltraSegnalazione(anomaliaBean);

        
        mostraStatus(messaggioRisultato, true);
        Printer.printf(messaggioRisultato);

        try {
            SceneManager.getInstance().cambiaScena(CostantiGUI.VIEW_MAGAZZINO);
        } catch (it.ortogest.ortogestapp.exception.ViewException e) {
            Printer.perror("Errore tornando al Magazzino: " + e.getMessage());
        }
    }

    @FXML
    public void annullaAction() {
        try {
            SceneManager.getInstance().cambiaScena(CostantiGUI.VIEW_MAGAZZINO);
        } catch (it.ortogest.ortogestapp.exception.ViewException e) {
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
