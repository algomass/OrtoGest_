package it.ortogest.ortogestapp.graphiccontroller;

import it.ortogest.ortogestapp.appcontroller.CreaOrdineAppController;
import it.ortogest.ortogestapp.beans.OrdineBean;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;


public class GestioneOrdiniOnlineGraphicController extends BaseGraphicController {

    private static final String STATO_INVIATO = "Inviato";
    private static final String FILTRO_TUTTI = "Tutti gli stati";

    @FXML private TableView<OrdineBean> ordiniTable;
    @FXML private TableColumn<OrdineBean, String> colIdOrdine;
    @FXML private TableColumn<OrdineBean, String> colCliente;
    @FXML private TableColumn<OrdineBean, Double> colTotale;
    @FXML private TableColumn<OrdineBean, String> colStato;
    @FXML private ComboBox<String> filtroStatoComboBox;

    @FXML private Label dettaglioClienteLabel;
    @FXML private Label dettaglioOrarioLabel;
    @FXML private ListView<String> dettaglioProdottiList;
    @FXML private Label messaggioLabel;
    @FXML private Button btnSegnaPronto;

    private CreaOrdineAppController appController;

    @FXML
    public void initialize() {
        fissaTabelle(ordiniTable);
        appController = CreaOrdineAppController.getInstance();

        colIdOrdine.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIdOrdine()));
        colCliente.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmailCliente()));
        
        
        colTotale.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("totale"));
        formatDoubleColumn(colTotale);
        colStato.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("stato"));
        
        filtroStatoComboBox.getItems().addAll(FILTRO_TUTTI, STATO_INVIATO, "Pronto per il Ritiro", "Ritirato");
        filtroStatoComboBox.setValue(FILTRO_TUTTI);
        filtroStatoComboBox.setOnAction(e -> caricaOrdini());

        ordiniTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                mostraDettagliOrdine(newSelection);
            }
        });

        caricaOrdini();
    }

    private void caricaOrdini() {
        List<OrdineBean> tuttiOrdini = appController.getTuttiOrdini();
        
        String filtro = filtroStatoComboBox != null ? filtroStatoComboBox.getValue() : FILTRO_TUTTI;
        List<OrdineBean> filtrati;
        
        if (FILTRO_TUTTI.equals(filtro) || filtro == null) {
            filtrati = tuttiOrdini;
        } else {
            filtrati = tuttiOrdini.stream()
                .filter(o -> filtro.equals(o.getStato()))
                .toList();
        }

        ObservableList<OrdineBean> data = FXCollections.observableArrayList(filtrati);
        ordiniTable.setItems(data);
        
        
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
        
        
        btnSegnaPronto.setDisable(!STATO_INVIATO.equals(ordine.getStato()));
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
        
        if (!STATO_INVIATO.equals(selected.getStato())) {
            mostraErrore("L'ordine non è nello stato Inviato.");
            return;
        }

        try {
            appController.aggiornaStatoOrdine(selected.getIdOrdine(), "Pronto per il Ritiro");
            mostraSuccesso("Stato aggiornato correttamente.");
            caricaOrdini(); 
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

    @FXML
    public void tornaIndietroAction() {
        try {
            it.ortogest.ortogestapp.utils.SceneManager.getInstance().cambiaScena(it.ortogest.ortogestapp.utils.CostantiGUI.VIEW_RESPONSABILE);
        } catch (it.ortogest.ortogestapp.exception.ViewException e) {
            mostraErrore("Errore nel ritorno alla schermata Responsabile: " + e.getMessage());
        }
    }
}
