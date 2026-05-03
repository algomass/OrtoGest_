package it.ortogest.ortogestapp.graphiccontroller;

import it.ortogest.ortogestapp.appcontroller.GestioneMagazzinoAppController;
import it.ortogest.ortogestapp.beans.LottoBean;
import it.ortogest.ortogestapp.exception.GestioneException;
import it.ortogest.ortogestapp.utils.Printer;
import it.ortogest.ortogestapp.utils.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.List;

public class RegistrazioneLottoGraphicController {

    @FXML private TextField idLottoField;
    @FXML private TextField fornitoreField;
    @FXML private ComboBox<String> prodottoComboBox;
    @FXML private TextField quantitaField;
    @FXML private DatePicker dataArrivoPicker;
    @FXML private DatePicker dataScadenzaPicker;
    @FXML private TextField costoAcquistoField;
    @FXML private Label errorLabel;

    @FXML
    public void initialize() {
        // Popolamento della ComboBox con i prodotti esistenti
        GestioneMagazzinoAppController appController = new GestioneMagazzinoAppController();
        List<String> nomi = appController.getNomiProdotti();
        ObservableList<String> nomiProdotti = FXCollections.observableArrayList(nomi);
        prodottoComboBox.setItems(nomiProdotti);
    }

    @FXML
    public void indietroAction() {
        try {
            SceneManager.getInstance().cambiaScena("/GUI/Magazzino.fxml");
        } catch (IOException e) {
            Printer.perror("Errore nel ritorno al magazzino: " + e.getMessage());
        }
    }

    @FXML
    public void segnalaAnomaliaAction() {
        try {
            SceneManager.getInstance().cambiaScena("/GUI/SegnalazioneAnomalia.fxml");
        } catch (IOException e) {
            Printer.perror("Errore nell'apertura della schermata di segnalazione: " + e.getMessage());
        }
    }

    @FXML
    public void salvaLottoAction() {
        errorLabel.setVisible(false);

        try {
            // 1. Lettura campi
            String idLotto = idLottoField.getText();
            String fornitore = fornitoreField.getText();
            // Usiamo getEditor().getText() per leggere anche i nomi digitati manualmente
            String prodotto = prodottoComboBox.getEditor().getText();
            String quantitaStr = quantitaField.getText();
            
            if (idLotto == null || idLotto.trim().isEmpty() || fornitore == null || fornitore.trim().isEmpty() || prodotto == null || prodotto.trim().isEmpty() || quantitaStr == null || quantitaStr.trim().isEmpty()) {
                throw new GestioneException("Tutti i campi testuali sono obbligatori.");
            }

            double quantita = parseDoubleOrThrow(quantitaStr, "La quantità deve essere un numero valido.");

            double costoAcquisto = 0.0;
            String costoStr = costoAcquistoField.getText();
            if (costoStr != null && !costoStr.trim().isEmpty()) {
                costoAcquisto = parseDoubleOrThrow(costoStr, "Il costo di acquisto deve essere un numero valido.");
            }

            // 2. Creazione Bean
            LottoBean lottoBean = new LottoBean(
                    idLotto, 
                    fornitore, 
                    prodotto, 
                    quantita, 
                    dataArrivoPicker.getValue(), 
                    dataScadenzaPicker.getValue(),
                    costoAcquisto
            );

            // 3. Invio all'App Controller
            GestioneMagazzinoAppController appController = new GestioneMagazzinoAppController();
            LottoBean risultato = appController.registraLotto(lottoBean);

            // 4. Successo
            Printer.printf("Lotto registrato con successo! ID: " + risultato.getIdLotto());
            
            // Torniamo alla home magazzino
            indietroAction();

        } catch (GestioneException e) {
            errorLabel.setText(e.getMessage());
            errorLabel.setVisible(true);
        } catch (Exception e) {
            errorLabel.setText("Errore imprevisto.");
            errorLabel.setVisible(true);
        }
    }

    private double parseDoubleOrThrow(String value, String errorMessage) throws GestioneException {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new GestioneException(errorMessage);
        }
    }
}
