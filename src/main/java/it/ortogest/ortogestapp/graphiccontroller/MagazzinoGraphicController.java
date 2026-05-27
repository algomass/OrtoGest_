package it.ortogest.ortogestapp.graphiccontroller;

import it.ortogest.ortogestapp.appcontroller.GestioneMagazzinoAppController;
import it.ortogest.ortogestapp.beans.ProdottoBean;
import it.ortogest.ortogestapp.utils.Printer;
import it.ortogest.ortogestapp.utils.CostantiGUI;
import it.ortogest.ortogestapp.utils.SceneManager;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.util.List;

/**
 * Controller Grafico (Boundary) per la schermata del Magazzino.
 */
public class MagazzinoGraphicController extends BaseGraphicController {

    @FXML
    private TableView<ProdottoBean> inventarioTable;
    @FXML
    private TableColumn<ProdottoBean, String> colNome;
    @FXML
    private TableColumn<ProdottoBean, Number> colPrezzo;
    @FXML
    private TableColumn<ProdottoBean, Number> colGiacenza;

    @FXML
    private TextField searchField;

    @FXML
    public void initialize() {
        // Configurazione delle colonne con le proprietà del ProdottoBean
        colNome.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNome()));
        colPrezzo.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrezzoAttuale()));
        colGiacenza.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getGiacenza()));

        setupRowFactory();
        caricaInventario();
    }

    private void setupRowFactory() {
        inventarioTable.setRowFactory(tv -> {
            javafx.scene.control.TableRow<ProdottoBean> row = new javafx.scene.control.TableRow<ProdottoBean>() {
                @Override
                protected void updateItem(ProdottoBean item, boolean empty) {
                    super.updateItem(item, empty);
                    applyRowStyle(this, item, empty);
                }
            };
            row.setOnMouseClicked(event -> handleRowClick(row, event));
            return row;
        });
    }

    private void applyRowStyle(javafx.scene.control.TableRow<ProdottoBean> row, ProdottoBean item, boolean empty) {
        if (item == null || empty) {
            row.setStyle("");
            return;
        }
        String filter = searchField != null && searchField.getText() != null ? searchField.getText().trim().toLowerCase() : "";
        if (!filter.isEmpty() && item.getNome().toLowerCase().contains(filter)) {
            row.setStyle("-fx-background-color: #ffcccc;");
        } else {
            row.setStyle("");
        }
    }

    private void handleRowClick(javafx.scene.control.TableRow<ProdottoBean> row, javafx.scene.input.MouseEvent event) {
        if (!row.isEmpty() && event.getButton() == javafx.scene.input.MouseButton.PRIMARY && event.getClickCount() == 2) {
            mostraLottiProdotto(row.getItem().getNome());
        }
    }

    @FXML
    public void cercaProdottoAction() {
        inventarioTable.refresh();
    }

    private void mostraLottiProdotto(String nomeProdotto) {
        GestioneMagazzinoAppController controller = new GestioneMagazzinoAppController();
        List<it.ortogest.ortogestapp.beans.LottoBean> lotti = controller.getLottiPerProdotto(nomeProdotto);
        
        StringBuilder sb = new StringBuilder();
        if (lotti.isEmpty()) {
            sb.append("Nessun lotto registrato per questo prodotto.");
        } else {
            for (it.ortogest.ortogestapp.beans.LottoBean l : lotti) {
                sb.append("ID Lotto: ").append(l.getIdLotto())
                  .append("\nQuantità: ").append(l.getQuantitaKg()).append(" Kg")
                  .append("\nArrivo: ").append(l.getDataArrivo())
                  .append("\nScadenza: ").append(l.getDataScadenza())
                  .append("\nFornitore: ").append(l.getNomeFornitore())
                  .append("\n\n");
            }
        }
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dettagli Lotti");
        alert.setHeaderText("Dati Documento Trasporto - " + nomeProdotto);
        alert.setContentText(sb.toString());
        alert.showAndWait();
    }

    private void caricaInventario() {
        GestioneMagazzinoAppController appController = new GestioneMagazzinoAppController();
        List<ProdottoBean> beans = appController.getInventario();
        ObservableList<ProdottoBean> data = FXCollections.observableArrayList(beans);
        inventarioTable.setItems(data);
    }

    @FXML
    public void apriSegnalazioneAnomaliaAction() {
        try {
            SceneManager.getInstance().cambiaScena(CostantiGUI.VIEW_SEGNALAZIONE_ANOMALIA);
        } catch (IOException e) {
            Printer.perror("Errore nell'apertura della schermata di segnalazione: " + e.getMessage());
        }
    }

    @FXML
    public void registraLottoAction() {
        try {
            SceneManager.getInstance().cambiaScena(CostantiGUI.VIEW_REGISTRAZIONE_LOTTO);
        } catch (IOException e) {
            Printer.perror("Errore nell'apertura della schermata di registrazione lotto: " + e.getMessage());
        }
    }
}
