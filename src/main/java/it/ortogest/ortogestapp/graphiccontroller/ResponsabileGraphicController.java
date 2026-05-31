package it.ortogest.ortogestapp.graphiccontroller;

import it.ortogest.ortogestapp.appcontroller.GestioneCatalogoAppController;
import it.ortogest.ortogestapp.beans.ProdottoBean;
import it.ortogest.ortogestapp.model.CategoriaProdotto;
import it.ortogest.ortogestapp.utils.Printer;
import it.ortogest.ortogestapp.utils.CostantiGUI;
import it.ortogest.ortogestapp.utils.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.List;

public class ResponsabileGraphicController {

    @FXML private TableView<ProdottoBean> tabellaProdotti;
    @FXML private TableColumn<ProdottoBean, String> colNome;
    @FXML private TableColumn<ProdottoBean, String> colCategoria;
    @FXML private TableColumn<ProdottoBean, Double> colGiacenza;
    @FXML private TableColumn<ProdottoBean, Double> colPrezzoAcquisto;
    @FXML private TableColumn<ProdottoBean, Double> colPrezzo;

    @FXML private TextField prodottoSelezionatoField;
    @FXML private ComboBox<String> nuovaCategoriaComboBox;
    @FXML private TextField nuovoPrezzoField;
    @FXML private Label messaggioLabel;

    private GestioneCatalogoAppController appController;

    @FXML
    public void initialize() {
        appController = new GestioneCatalogoAppController();

        // Coloriamo di rosso i prodotti "Da prezzare" (prezzo 0.0)
        colPrezzo.setCellFactory(column -> new TableCell<ProdottoBean, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("%.2f", item));
                    if (item == 0.0) {
                        setTextFill(Color.RED);
                        setStyle("-fx-font-weight: bold;");
                    } else {
                        setTextFill(Color.BLACK);
                        setStyle("");
                    }
                }
            }
        });

        // Inizializziamo la ComboBox delle categorie
        nuovaCategoriaComboBox.getItems().addAll(CategoriaProdotto.FRUTTA, CategoriaProdotto.VERDURA);

        // Listener per la selezione nella tabella
        tabellaProdotti.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                prodottoSelezionatoField.setText(newSelection.getNome());
                nuovoPrezzoField.setText(String.valueOf(newSelection.getPrezzoAttuale()));
                nuovaCategoriaComboBox.setValue(newSelection.getCategoria());
            }
        });

        caricaCatalogo();
    }

    private void caricaCatalogo() {
        List<ProdottoBean> prodotti = appController.getTuttiIProdotti();
        ObservableList<ProdottoBean> observableList = FXCollections.observableArrayList(prodotti);
        tabellaProdotti.setItems(observableList);
    }

    @FXML
    public void aggiornaPrezzoAction() {
        messaggioLabel.setVisible(false);
        String nomeProdotto = prodottoSelezionatoField.getText();
        String prezzoStr = nuovoPrezzoField.getText();
        String categoriaSelezionata = nuovaCategoriaComboBox.getValue();

        if (nomeProdotto == null || nomeProdotto.isEmpty()) {
            mostraErrore("Seleziona prima un prodotto dalla tabella.");
            return;
        }
        
        if (categoriaSelezionata == null) {
            mostraErrore("Seleziona una categoria valida.");
            return;
        }

        try {
            double nuovoPrezzo = Double.parseDouble(prezzoStr);

            ProdottoBean bean = new ProdottoBean();
            bean.setNome(nomeProdotto);
            bean.setPrezzoAttuale(nuovoPrezzo);
            bean.setCategoria(categoriaSelezionata);

            appController.aggiornaPrezzoProdotto(bean);

            mostraSuccesso("prezzo modificato correttamente");
            caricaCatalogo(); // Ricarichiamo la tabella per mostrare il nuovo prezzo

        } catch (NumberFormatException e) {
            mostraErrore("Inserisci un valore numerico valido per il prezzo (es. 2.50).");
        } catch (Exception e) {
            mostraErrore(e.getMessage());
        }
    }

    @FXML
    public void logoutAction() {
        try {
            // Effettuiamo un logout pulendo eventualmente la sessione (da implementare nel SessionManager se necessario)
            SceneManager.getInstance().cambiaScena(CostantiGUI.VIEW_LOGIN);
        } catch (IOException e) {
            Printer.perror("Errore nel ritorno al login: " + e.getMessage());
        }
    }

    private void mostraErrore(String msg) {
        messaggioLabel.setText(msg);
        messaggioLabel.setTextFill(Color.web("#e74c3c")); // Rosso
        messaggioLabel.setVisible(true);
    }

    private void mostraSuccesso(String msg) {
        messaggioLabel.setText(msg);
        messaggioLabel.setTextFill(Color.web("#27ae60")); // Verde
        messaggioLabel.setVisible(true);
    }
}
