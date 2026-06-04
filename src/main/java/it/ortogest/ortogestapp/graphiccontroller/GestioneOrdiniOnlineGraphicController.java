package it.ortogest.ortogestapp.graphiccontroller;

import it.ortogest.ortogestapp.appcontroller.GestioneOrdiniAppController;
import it.ortogest.ortogestapp.beans.OrdineBean;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

/**
 * Controller Grafico (Boundary) per la schermata Ordini Online.
 */
public class GestioneOrdiniOnlineGraphicController extends BaseGraphicController {

    @FXML private TableView<OrdineBean> ordiniTable;
    @FXML private TableColumn<OrdineBean, String> colIdOrdine;
    @FXML private TableColumn<OrdineBean, String> colCliente;
    @FXML private TableColumn<OrdineBean, String> colOrario;
    @FXML private TableColumn<OrdineBean, Double> colTotale;
    @FXML private TableColumn<OrdineBean, String> colStato;

    @FXML private Label dettaglioClienteLabel;
    @FXML private Label dettaglioOrarioLabel;
    @FXML private ListView<String> dettaglioProdottiList;
    @FXML private Label messaggioLabel;
    @FXML private Button btnSegnaPronto;

    private GestioneOrdiniAppController appController;

    @FXML
    public void initialize() {
        appController = new GestioneOrdiniAppController();

        colIdOrdine.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIdOrdine()));
        colCliente.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmailCliente()));
        colOrario.setCellValueFactory(cellData -> new SimpleStringProperty("Oggi")); // Placeholder
        
        // Formattazione per la colonna totale
        colTotale.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("totale"));
        colStato.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("stato"));

        ordiniTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                mostraDettagliOrdine(newSelection);
            }
        });

        caricaOrdini();
    }

    private void caricaOrdini() {
        List<OrdineBean> tuttiOrdini = appController.getTuttiOrdini();
        ObservableList<OrdineBean> data = FXCollections.observableArrayList(tuttiOrdini);
        ordiniTable.setItems(data);
        
        // Deseleziona e pulisci dettagli se la lista viene ricaricata
        ordiniTable.getSelectionModel().clearSelection();
        pulisciDettagli();
    }

    private void mostraDettagliOrdine(OrdineBean ordine) {
        dettaglioClienteLabel.setText(ordine.getEmailCliente() + " (Ord. #" + ordine.getIdOrdine() + ")");
        dettaglioOrarioLabel.setText("Stato: " + ordine.getStato());
        
        dettaglioProdottiList.getItems().clear();
        String[] righe = ordine.getRiepilogoProdotti().split(", ");
        for (String r : righe) {
            dettaglioProdottiList.getItems().add(r);
        }
        
        // Abilita il pulsante solo se l'ordine è "Inviato"
        btnSegnaPronto.setDisable(!"Inviato".equals(ordine.getStato()));
        messaggioLabel.setText("");
    }
    
    private void pulisciDettagli() {
        dettaglioClienteLabel.setText("Seleziona un ordine...");
        dettaglioOrarioLabel.setText("");
        dettaglioProdottiList.getItems().clear();
        btnSegnaPronto.setDisable(true);
        messaggioLabel.setText("");
    }

    @FXML
    public void segnaComeProntoAction() {
        OrdineBean selected = ordiniTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            mostraErrore("Seleziona un ordine dalla tabella.");
            return;
        }
        
        if (!"Inviato".equals(selected.getStato())) {
            mostraErrore("L'ordine non è nello stato Inviato.");
            return;
        }

        try {
            appController.aggiornaStatoOrdine(selected.getIdOrdine(), "Pronto per il Ritiro");
            mostraSuccesso("Stato aggiornato correttamente.");
            caricaOrdini(); // Ricarica la tabella
        } catch (Exception e) {
            mostraErrore("Errore nell'aggiornamento: " + e.getMessage());
        }
    }

    private void mostraErrore(String msg) {
        mostraStatusLabel(messaggioLabel, msg, false);
    }

    private void mostraSuccesso(String msg) {
        mostraStatusLabel(messaggioLabel, msg, true);
    }
}
