package it.ortogest.ortogestapp.graphiccontroller;

import it.ortogest.ortogestapp.appcontroller.CreaOrdineAppController;
import it.ortogest.ortogestapp.beans.OrdineBean;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;


public class CassaGraphicController extends BaseGraphicController {

    @FXML private TableView<OrdineBean> ordiniTable;
    @FXML private TableColumn<OrdineBean, String> colIdOrdine;
    @FXML private TableColumn<OrdineBean, String> colCliente;
    @FXML private TableColumn<OrdineBean, Double> colTotale;

    @FXML private Label totaleLabel;
    @FXML private Button btnEmettiScontrino;

    private CreaOrdineAppController appController;
    
    
    private OrdineBean ordineInPagamento = null;

    @FXML
    public void initialize() {
        fissaTabelle(ordiniTable);
        appController = new CreaOrdineAppController();

        if (colIdOrdine != null) {
            colIdOrdine.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIdOrdine()));
            colCliente.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmailCliente()));
            colTotale.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("totale"));
            formatDoubleColumn(colTotale);
            
            caricaOrdiniPronti();
        }
    }

    private void caricaOrdiniPronti() {
        List<OrdineBean> pronti = appController.getOrdiniProntiPerRitiro();
                
        ObservableList<OrdineBean> data = FXCollections.observableArrayList(pronti);
        ordiniTable.setItems(data);
    }

    @FXML
    public void caricaOrdineAction() {
        OrdineBean selected = ordiniTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            mostraAlertErrore("Seleziona un ordine Pronto per il Ritiro dalla tabella.");
            return;
        }

        this.ordineInPagamento = selected;
        totaleLabel.setText(String.format("EUR %.2f", selected.getTotale()));
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Ordine #" + selected.getIdOrdine() + " caricato in cassa.", ButtonType.OK);
        alert.setHeaderText("Ordine Caricato");
        alert.showAndWait();
    }

    @FXML
    public void emettiScontrinoAction() {
        if (ordineInPagamento != null) {
            try {
                
                appController.registraVendita(ordineInPagamento.getIdOrdine());
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Pagamento completato. L'ordine #" + ordineInPagamento.getIdOrdine() + " è ora Ritirato.", ButtonType.OK);
                alert.setHeaderText("Transazione Eseguita");
                alert.showAndWait();
                
                annullaOperazioneAction(); 
                caricaOrdiniPronti(); 
            } catch (Exception e) {
                mostraAlertErrore("Errore durante il pagamento: " + e.getMessage());
            }
        } else {
            mostraAlertErrore("Nessun ordine online selezionato per il pagamento.");
        }
    }
    
    @FXML
    public void annullaOperazioneAction() {
        this.ordineInPagamento = null;
        totaleLabel.setText("EUR 0,00");
        if (ordiniTable != null) {
            ordiniTable.getSelectionModel().clearSelection();
        }
    }
    
    private void mostraAlertErrore(String msg) {
        Alert errAlert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        errAlert.setHeaderText("Attenzione");
        errAlert.showAndWait();
    }
}
