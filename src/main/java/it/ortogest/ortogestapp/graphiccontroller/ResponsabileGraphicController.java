package it.ortogest.ortogestapp.graphiccontroller;

import it.ortogest.ortogestapp.appcontroller.GestioneCatalogoAppController;
import it.ortogest.ortogestapp.beans.LottoBean;
import it.ortogest.ortogestapp.beans.ProdottoBean;
import it.ortogest.ortogestapp.model.CategoriaProdotto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.List;

public class ResponsabileGraphicController extends BaseGraphicController {

    private boolean isVistaScontiAttiva = false;

    @FXML
    private Button btnToggleVista;

    @FXML
    private VBox sezioneSuperiore;

    @FXML
    private TableView<ProdottoBean> tabellaProdotti;
    @FXML
    private TableColumn<ProdottoBean, String> colNome;
    @FXML
    private TableColumn<ProdottoBean, String> colCategoria;
    @FXML
    private TableColumn<ProdottoBean, Double> colGiacenza;

    @FXML
    private TextField prodottoSelezionatoField;
    @FXML
    private ComboBox<String> nuovaCategoriaComboBox;

    @FXML
    private TableView<LottoBean> tabellaLotti;
    @FXML
    private TableColumn<LottoBean, String> colIdLotto;
    @FXML
    private TableColumn<LottoBean, String> colNomeProdottoLotto;
    @FXML
    private TableColumn<LottoBean, LocalDate> colDataScadenza;
    @FXML
    private TableColumn<LottoBean, Double> colQuantitaLotto;
    @FXML
    private TableColumn<LottoBean, Double> colCostoAcquisto;
    @FXML
    private TableColumn<LottoBean, Double> colPrezzoVendita;
    @FXML
    private TableColumn<LottoBean, Boolean> colScontoAttivo;
    @FXML
    private TableColumn<LottoBean, Double> colPrezzoScontato;

    @FXML
    private TextField lottoSelezionatoField;
    @FXML
    private TextField prezzoVenditaField;
    @FXML
    private CheckBox scontoCheckBox;
    @FXML
    private TextField prezzoScontatoField;

    @FXML
    private Label messaggioLabel;

    private GestioneCatalogoAppController appController;

    @FXML
    public void initialize() {
        appController = new GestioneCatalogoAppController();

        // Coloriamo di rosso i lotti "Da prezzare" (prezzo vendita 0.0)
        colPrezzoVendita.setCellFactory(column -> new TableCell<LottoBean, Double>() {
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

        nuovaCategoriaComboBox.getItems().addAll(CategoriaProdotto.FRUTTA, CategoriaProdotto.VERDURA);

        // Listener per la selezione nella tabella prodotti
        tabellaProdotti.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                prodottoSelezionatoField.setText(newSelection.getNome());
                nuovaCategoriaComboBox.setValue(newSelection.getCategoria());
                caricaLotti(newSelection.getNome());
                pulisciFormLotto();
            }
        });

        // Listener per la selezione nella tabella lotti
        tabellaLotti.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                lottoSelezionatoField.setText(newSelection.getIdLotto());
                prezzoVenditaField.setText(String.valueOf(newSelection.getPrezzoVendita()));
                scontoCheckBox.setSelected(newSelection.isScontoScadenzaAttivo());
                prezzoScontatoField.setText(String.valueOf(newSelection.getPrezzoScontato()));
            }
        });

        // Gestione abilitazione/disabilitazione campo sconto
        prezzoScontatoField.setDisable(true);
        scontoCheckBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            prezzoScontatoField.setDisable(!isNowSelected);
        });

        caricaCatalogo();
    }

    private void caricaCatalogo() {
        List<ProdottoBean> prodotti = appController.getTuttiIProdotti();
        ObservableList<ProdottoBean> observableList = FXCollections.observableArrayList(prodotti);
        tabellaProdotti.setItems(observableList);

        // Manteniamo la selezione se possibile
        String prodSelezionato = prodottoSelezionatoField.getText();
        if (prodSelezionato != null && !prodSelezionato.isEmpty()) {
            caricaLotti(prodSelezionato);
        } else {
            tabellaLotti.setItems(FXCollections.observableArrayList());
            pulisciFormLotto();
        }
    }

    private void caricaLotti(String nomeProdotto) {
        List<LottoBean> lotti = appController.getLottiPerProdotto(nomeProdotto);
        ObservableList<LottoBean> observableList = FXCollections.observableArrayList(lotti);
        tabellaLotti.setItems(observableList);
    }

    private void pulisciFormLotto() {
        lottoSelezionatoField.clear();
        prezzoVenditaField.clear();
        scontoCheckBox.setSelected(false);
        prezzoScontatoField.clear();
    }

    @FXML
    public void aggiornaCategoriaAction() {
        messaggioLabel.setVisible(false);
        String nomeProdotto = prodottoSelezionatoField.getText();
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
            ProdottoBean bean = new ProdottoBean();
            bean.setNome(nomeProdotto);
            bean.setCategoria(categoriaSelezionata);

            appController.aggiornaCategoriaProdotto(bean);

            mostraSuccesso("Categoria aggiornata correttamente");
            caricaCatalogo();

        } catch (Exception e) {
            mostraErrore(e.getMessage());
        }
    }

    @FXML
    public void aggiornaPrezzoLottoAction() {
        messaggioLabel.setVisible(false);
        String idLotto = lottoSelezionatoField.getText();

        if (idLotto == null || idLotto.isEmpty()) {
            mostraErrore("Seleziona prima un lotto dalla tabella.");
            return;
        }

        try {
            double nuovoPrezzo = Double.parseDouble(prezzoVenditaField.getText());
            boolean scontoAttivo = scontoCheckBox.isSelected();
            double prezzoScontato = 0.0;

            if (scontoAttivo) {
                prezzoScontato = Double.parseDouble(prezzoScontatoField.getText());
                if (prezzoScontato >= nuovoPrezzo) {
                    mostraErrore("Il prezzo dello sconto deve essere piÃƒÂ¹ basso di quello di vendita.");
                    return;
                }
            }

            LottoBean bean = new LottoBean();
            bean.setIdLotto(idLotto);
            bean.setPrezzoVendita(nuovoPrezzo);
            bean.setScontoScadenzaAttivo(scontoAttivo);
            bean.setPrezzoScontato(prezzoScontato);

            appController.aggiornaPrezzoLotto(bean);

            mostraSuccesso("Prezzi del lotto aggiornati correttamente");

            // Ricarichiamo i lotti del prodotto corrente o la lista scadenze
            if (!isVistaScontiAttiva) {
                String prodSelezionato = prodottoSelezionatoField.getText();
                if (prodSelezionato != null && !prodSelezionato.isEmpty()) {
                    caricaLotti(prodSelezionato);
                }
            } else {
                List<LottoBean> lottiInScadenza = appController.getLottiInScadenza(2);
                tabellaLotti.setItems(FXCollections.observableArrayList(lottiInScadenza));
            }

        } catch (NumberFormatException _) {
            mostraErrore("Inserisci un valore numerico valido per il prezzo (es. 2.50).");
        } catch (Exception e) {
            mostraErrore(e.getMessage());
        }
    }

    @FXML
    public void apriGestioneOrdiniAction() {
        try {
            it.ortogest.ortogestapp.utils.SceneManager.getInstance().cambiaScena(it.ortogest.ortogestapp.utils.CostantiGUI.VIEW_GESTIONE_ORDINI_ONLINE);
        } catch (java.io.IOException e) {
            mostraErrore("Errore nell'apertura della gestione ordini: " + e.getMessage());
        }
    }

    @FXML
    public void toggleVistaAction() {
        messaggioLabel.setVisible(false);
        
        if (!isVistaScontiAttiva) {
            // Passa alla vista "Prodotti da scontare"
            try {
                List<LottoBean> lottiInScadenza = appController.getLottiInScadenza(2); // 48 ore di preavviso
                if (lottiInScadenza.isEmpty()) {
                    mostraSuccesso("Nessun prodotto in scadenza nelle prossime 48 ore.");
                } else {
                    mostraSuccesso("Trovati " + lottiInScadenza.size() + " lotti da scontare.");
                }
                
                // Popoliamo la tabella dei lotti
                ObservableList<LottoBean> observableList = FXCollections.observableArrayList(lottiInScadenza);
                tabellaLotti.setItems(observableList);
                
                sezioneSuperiore.setVisible(false);
                sezioneSuperiore.setManaged(false);
                btnToggleVista.setText("Vista classica");
                
                // Deselezioniamo il prodotto corrente per indicare che stiamo vedendo una vista globale
                tabellaProdotti.getSelectionModel().clearSelection();
                prodottoSelezionatoField.clear();
                nuovaCategoriaComboBox.setValue(null);
                
                pulisciFormLotto();
                
                isVistaScontiAttiva = true;
                
            } catch (Exception e) {
                mostraErrore("Errore durante il recupero dei lotti: " + e.getMessage());
            }
        } else {
            // Ritorna alla vista classica
            sezioneSuperiore.setVisible(true);
            sezioneSuperiore.setManaged(true);
            btnToggleVista.setText("Prodotti da scontare");
            isVistaScontiAttiva = false;
            
            pulisciFormLotto();
            caricaCatalogo();
        }
    }

    @FXML
    @Override
    public void logoutAction() {
        super.logoutAction();
    }

    private void mostraErrore(String msg) {
        mostraStatusLabel(messaggioLabel, msg, false);
    }

    private void mostraSuccesso(String msg) {
        mostraStatusLabel(messaggioLabel, msg, true);
    }
}
