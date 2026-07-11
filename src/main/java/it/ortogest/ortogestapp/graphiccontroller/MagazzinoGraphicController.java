package it.ortogest.ortogestapp.graphiccontroller;

import it.ortogest.ortogestapp.appcontroller.RegistraLottoAppController;
import it.ortogest.ortogestapp.beans.ProdottoBean;
import it.ortogest.ortogestapp.utils.Printer;
import it.ortogest.ortogestapp.utils.CostantiGUI;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import java.time.LocalDate;
import java.util.Optional;
import it.ortogest.ortogestapp.beans.LottoBean;
import it.ortogest.ortogestapp.exception.GestioneException;

import java.util.List;


public class MagazzinoGraphicController extends BaseGraphicController {

    private static final String ROW_ERROR = "row-error";

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
        fissaTabelle(inventarioTable);
        
        colNome.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNome()));
        colPrezzo.setCellValueFactory(
                cellData -> new SimpleDoubleProperty(cellData.getValue().getPrezzoAcquistoMedio()));
        colGiacenza.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getGiacenza()));

        formatDoubleColumn(colPrezzo);
        formatDoubleColumn(colGiacenza);

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
        String filter = searchField != null && searchField.getText() != null
                ? searchField.getText().trim().toLowerCase()
                : "";
        if (!filter.isEmpty() && item.getNome().toLowerCase().contains(filter)) {
            if (!row.getStyleClass().contains(ROW_ERROR)) {
                row.getStyleClass().add(ROW_ERROR);
            }
        } else {
            row.getStyleClass().remove(ROW_ERROR);
        }
    }

    private void handleRowClick(javafx.scene.control.TableRow<ProdottoBean> row, javafx.scene.input.MouseEvent event) {
        if (!row.isEmpty() && event.getButton() == javafx.scene.input.MouseButton.PRIMARY
                && event.getClickCount() == 2) {
            mostraLottiProdotto(row.getItem().getNome());
        }
    }

    @FXML
    public void cercaProdottoAction() {
        inventarioTable.refresh();
    }

    private void mostraLottiProdotto(String nomeProdotto) {
        RegistraLottoAppController controller = RegistraLottoAppController.getInstance();

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Gestione Lotti");
        dialog.setHeaderText("Lotti registrati per: " + nomeProdotto);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().setPrefWidth(600);

        TableView<LottoBean> table = new TableView<>();
        ObservableList<LottoBean> data = FXCollections
                .observableArrayList(controller.getLottiPerProdotto(nomeProdotto));
        table.setItems(data);

        TableColumn<LottoBean, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idLotto"));
        colId.setPrefWidth(80);

        TableColumn<LottoBean, String> colFornitore = new TableColumn<>("Fornitore");
        colFornitore.setCellValueFactory(new PropertyValueFactory<>("nomeFornitore"));
        colFornitore.setPrefWidth(120);

        TableColumn<LottoBean, Double> colQuantita = new TableColumn<>("Quantità (Kg)");
        colQuantita.setCellValueFactory(new PropertyValueFactory<>("quantitaKg"));
        colQuantita.setPrefWidth(100);

        TableColumn<LottoBean, LocalDate> colArrivo = new TableColumn<>("Arrivo");
        colArrivo.setCellValueFactory(new PropertyValueFactory<>("dataArrivo"));
        colArrivo.setPrefWidth(100);

        TableColumn<LottoBean, LocalDate> colScadenza = new TableColumn<>("Scadenza");
        colScadenza.setCellValueFactory(new PropertyValueFactory<>("dataScadenza"));
        colScadenza.setPrefWidth(100);

        TableColumn<LottoBean, Double> colCosto = new TableColumn<>("Costo Acquisto");
        colCosto.setCellValueFactory(new PropertyValueFactory<>("costoAcquisto"));
        colCosto.setPrefWidth(100);

        formatDoubleColumn(colQuantita);
        formatDoubleColumn(colCosto);

        table.getColumns().add(colId);
        table.getColumns().add(colFornitore);
        table.getColumns().add(colQuantita);
        table.getColumns().add(colCosto);
        table.getColumns().add(colArrivo);
        table.getColumns().add(colScadenza);
        
        fissaTabelle(table);

        Button btnModifica = new Button("Modifica");
        Button btnElimina = new Button("Elimina");

        btnModifica.setOnAction(e -> {
            LottoBean selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                apriModificaLottoDialog(selected, nomeProdotto, controller);
                table.setItems(FXCollections.observableArrayList(controller.getLottiPerProdotto(nomeProdotto)));
                caricaInventario();
            }
        });

        btnElimina.setOnAction(e -> {
            LottoBean selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                try {
                    controller.eliminaLotto(selected.getIdLotto());
                    List<LottoBean> lottiRimanenti = controller.getLottiPerProdotto(nomeProdotto);
                    table.setItems(FXCollections.observableArrayList(lottiRimanenti));
                    caricaInventario();
                    Printer.printf("Lotto eliminato con successo.");

                    if (lottiRimanenti.isEmpty()) {
                        dialog.setResult(null);
                        dialog.close();
                    }
                } catch (GestioneException _) {
                    Alert err = new Alert(Alert.AlertType.ERROR, "Errore durante l'operazione sul lotto.");
                    err.showAndWait();
                }
            }
        });

        HBox buttonBox = new HBox(10, btnModifica, btnElimina);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        VBox vbox = new VBox(table, buttonBox);
        vbox.setPadding(new Insets(10));

        dialog.getDialogPane().setContent(vbox);
        dialog.showAndWait();
    }

    private void apriModificaLottoDialog(LottoBean lotto, String nomeProdotto,
            RegistraLottoAppController controller) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modifica Lotto");
        dialog.setHeaderText("Modifica lotto ID: " + lotto.getIdLotto());

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField txtFornitore = new TextField(lotto.getNomeFornitore());
        TextField txtQuantita = new TextField(String.valueOf(lotto.getQuantitaKg()));
        DatePicker dpArrivo = new DatePicker(lotto.getDataArrivo());
        DatePicker dpScadenza = new DatePicker(lotto.getDataScadenza());
        TextField txtCosto = new TextField(String.valueOf(lotto.getCostoAcquisto()));

        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(
                new Label("Fornitore:"), txtFornitore,
                new Label("Quantità (Kg):"), txtQuantita,
                new Label("Data Arrivo:"), dpArrivo,
                new Label("Data Scadenza:"), dpScadenza,
                new Label("Costo Acquisto:"), txtCosto);
        dialog.getDialogPane().setContent(vbox);

        Optional<ButtonType> res = dialog.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            try {
                LottoBean nuovo = LottoBean.builder()
                        .idLotto(lotto.getIdLotto())
                        .nomeFornitore(txtFornitore.getText())
                        .nomeProdotto(nomeProdotto)
                        .quantitaKg(Double.parseDouble(txtQuantita.getText()))
                        .dataArrivo(dpArrivo.getValue())
                        .dataScadenza(dpScadenza.getValue())
                        .costoAcquisto(Double.parseDouble(txtCosto.getText()))
                        .build();
                controller.modificaLotto(nuovo);
                Printer.printf("Lotto modificato con successo.");
            } catch (NumberFormatException _) {
                new Alert(Alert.AlertType.ERROR, "Valori numerici non validi.").showAndWait();
            } catch (GestioneException _) {
                new Alert(Alert.AlertType.ERROR, "Errore durante la modifica del lotto.").showAndWait();
            }
        }
    }

    private void caricaInventario() {
        RegistraLottoAppController appController = RegistraLottoAppController.getInstance();
        List<ProdottoBean> beans = appController.getInventario();
        ObservableList<ProdottoBean> data = FXCollections.observableArrayList(beans);
        inventarioTable.setItems(data);
    }

    @FXML
    public void apriSegnalazioneAnomaliaAction() {
        cambiaScenaSicuro(CostantiGUI.VIEW_SEGNALAZIONE_ANOMALIA,
                "Errore nell'apertura della schermata di segnalazione:");
    }

    @FXML
    public void registraLottoAction() {
        cambiaScenaSicuro(CostantiGUI.VIEW_REGISTRAZIONE_LOTTO,
                "Errore nell'apertura della schermata di registrazione lotto:");
    }

    @FXML
    public void apriDaSmaltireAction() {
        RegistraLottoAppController controller = RegistraLottoAppController.getInstance();
        List<LottoBean> lottiDaSmaltire = controller.getLottiDaSmaltire();

        if (lottiDaSmaltire.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "Non ci sono lotti da smaltire al momento.").showAndWait();
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Lotti da Smaltire");
        dialog.setHeaderText("Questi lotti sono scaduti e vanno smaltiti fisicamente.");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().setPrefWidth(600);

        TableView<LottoBean> table = new TableView<>();
        ObservableList<LottoBean> data = FXCollections.observableArrayList(lottiDaSmaltire);
        table.setItems(data);

        TableColumn<LottoBean, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idLotto"));
        colId.setPrefWidth(80);

        TableColumn<LottoBean, String> colProdotto = new TableColumn<>("Prodotto");
        colProdotto.setCellValueFactory(new PropertyValueFactory<>("nomeProdotto"));
        colProdotto.setPrefWidth(200);

        TableColumn<LottoBean, Double> colQuantita = new TableColumn<>("Quantità (Kg)");
        colQuantita.setCellValueFactory(new PropertyValueFactory<>("quantitaKg"));
        colQuantita.setPrefWidth(120);
        formatDoubleColumn(colQuantita);

        TableColumn<LottoBean, LocalDate> colScadenza = new TableColumn<>("Scadenza");
        colScadenza.setCellValueFactory(new PropertyValueFactory<>("dataScadenza"));
        colScadenza.setPrefWidth(130);

        table.getColumns().add(colId);
        table.getColumns().add(colProdotto);
        table.getColumns().add(colQuantita);
        table.getColumns().add(colScadenza);
        
        fissaTabelle(table);

        Button btnSmaltisci = new Button("Smaltisci Selezionato");
        btnSmaltisci.getStyleClass().add("btn-purple");
        btnSmaltisci.setOnAction(e -> {
            LottoBean selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                try {
                    controller.smaltisciLotto(selected.getIdLotto());
                    data.remove(selected);
                    table.refresh();
                    caricaInventario(); 
                    Printer.printf("Lotto smaltito con successo.");

                    if (data.isEmpty()) {
                        dialog.setResult(null);
                        dialog.close();
                    }
                } catch (GestioneException _) {
                    new Alert(Alert.AlertType.ERROR, "Errore durante lo smaltimento del lotto.").showAndWait();
                }
            } else {
                new Alert(Alert.AlertType.WARNING, "Seleziona prima un lotto dalla tabella.").showAndWait();
            }
        });

        VBox vbox = new VBox(10, table, btnSmaltisci);
        vbox.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(vbox);
        dialog.showAndWait();
    }
}
