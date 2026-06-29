package it.ortogest.ortogestapp.graphiccontroller;

import it.ortogest.ortogestapp.appcontroller.GestisciCatalogoAppController;
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

    private enum StatoVista {
        CLASSICA, SCONTI, DA_PREZZARE, RITIRATI
    }
    private StatoVista vistaCorrente = StatoVista.CLASSICA;

    @FXML
    private Button btnToggleVista;

    @FXML
    private Button btnDaPrezzare;

    @FXML
    private Button btnRitirati;

    @FXML
    private Button btnRimuoviLotto;

    @FXML
    private Button btnRimettiInCommercio;

    @FXML
    private CheckBox chkMostraStorico;

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

    private GestisciCatalogoAppController appController;

    @FXML
    public void initialize() {
        appController = new GestisciCatalogoAppController();

        formatDoubleColumn(colGiacenza);
        formatDoubleColumn(colQuantitaLotto);
        formatDoubleColumn(colCostoAcquisto);
        formatDoubleColumn(colPrezzoScontato);

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
        boolean includiStorico = chkMostraStorico != null && chkMostraStorico.isSelected();
        List<ProdottoBean> prodotti = appController.getTuttiIProdotti(includiStorico);
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
        boolean includiStorico = chkMostraStorico != null && chkMostraStorico.isSelected();
        List<LottoBean> lotti = appController.getLottiPerProdotto(nomeProdotto, includiStorico);
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
    public void aggiornaTabelleAction() {
        caricaCatalogo();
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
                    mostraErrore("Il prezzo dello sconto deve essere più basso di quello di vendita.");
                    return;
                }
            }

            LottoBean bean = new LottoBean();
            bean.setIdLotto(idLotto);
            bean.setPrezzoVendita(nuovoPrezzo);
            bean.setScontoScadenzaAttivo(scontoAttivo);
            bean.setPrezzoScontato(prezzoScontato);

            if (vistaCorrente == StatoVista.DA_PREZZARE) {
                appController.impostaPrezzoLotto(bean);
            } else {
                appController.aggiornaPrezzoLotto(bean);
            }

            mostraSuccesso("Prezzi del lotto aggiornati correttamente");

            // Ricarichiamo i lotti del prodotto corrente o la lista scadenze/da prezzare
            if (vistaCorrente == StatoVista.CLASSICA) {
                String prodSelezionato = prodottoSelezionatoField.getText();
                if (prodSelezionato != null && !prodSelezionato.isEmpty()) {
                    caricaLotti(prodSelezionato);
                }
            } else if (vistaCorrente == StatoVista.SCONTI) {
                List<LottoBean> lottiInScadenza = appController.getLottiInScadenza(2);
                tabellaLotti.setItems(FXCollections.observableArrayList(lottiInScadenza));
            } else if (vistaCorrente == StatoVista.DA_PREZZARE) {
                List<LottoBean> lottiDaPrezzare = appController.getLottiDaPrezzare();
                tabellaLotti.setItems(FXCollections.observableArrayList(lottiDaPrezzare));
            } else if (vistaCorrente == StatoVista.RITIRATI) {
                List<LottoBean> lottiRitirati = appController.getLottiRitirati();
                tabellaLotti.setItems(FXCollections.observableArrayList(lottiRitirati));
            }

        } catch (NumberFormatException _) {
            mostraErrore("Inserisci un valore numerico valido per il prezzo (es. 2.50).");
        } catch (Exception e) {
            mostraErrore(e.getMessage());
        }
    }

    @FXML
    public void rimuoviLottoAction() {
        messaggioLabel.setVisible(false);
        String idLotto = lottoSelezionatoField.getText();

        if (idLotto == null || idLotto.isEmpty()) {
            mostraErrore("Seleziona prima un lotto dalla tabella da rimuovere.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma Rimozione");
        alert.setHeaderText("Rimozione lotto dal catalogo");
        alert.setContentText("Sei sicuro di voler rimuovere il lotto " + idLotto + "? Lo stato verrà impostato su RITIRATO.");

        java.util.Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                appController.rimuoviLottoDalCatalogo(idLotto);
                mostraSuccesso("Lotto " + idLotto + " rimosso con successo.");
                
                // Ricarica la tabella
                if (vistaCorrente == StatoVista.CLASSICA) {
                    String prodSelezionato = prodottoSelezionatoField.getText();
                    if (prodSelezionato != null && !prodSelezionato.isEmpty()) {
                        caricaLotti(prodSelezionato);
                    }
                } else if (vistaCorrente == StatoVista.SCONTI) {
                    List<LottoBean> lottiInScadenza = appController.getLottiInScadenza(2);
                    tabellaLotti.setItems(FXCollections.observableArrayList(lottiInScadenza));
                } else if (vistaCorrente == StatoVista.DA_PREZZARE) {
                    List<LottoBean> lottiDaPrezzare = appController.getLottiDaPrezzare();
                    tabellaLotti.setItems(FXCollections.observableArrayList(lottiDaPrezzare));
                } else if (vistaCorrente == StatoVista.RITIRATI) {
                    List<LottoBean> lottiRitirati = appController.getLottiRitirati();
                    tabellaLotti.setItems(FXCollections.observableArrayList(lottiRitirati));
                }
                
                pulisciFormLotto();
            } catch (Exception e) {
                mostraErrore("Errore durante la rimozione: " + e.getMessage());
            }
        }
    }

    @FXML
    public void rimettiInCommercioAction() {
        messaggioLabel.setVisible(false);
        String idLotto = lottoSelezionatoField.getText();

        if (idLotto == null || idLotto.isEmpty()) {
            mostraErrore("Seleziona prima un lotto dalla tabella da rimettere in commercio.");
            return;
        }

        try {
            double nuovoPrezzo = Double.parseDouble(prezzoVenditaField.getText());
            boolean scontoAttivo = scontoCheckBox.isSelected();
            double prezzoScontato = 0.0;

            if (scontoAttivo) {
                prezzoScontato = Double.parseDouble(prezzoScontatoField.getText());
                if (prezzoScontato >= nuovoPrezzo) {
                    mostraErrore("Il prezzo dello sconto deve essere più basso di quello di vendita.");
                    return;
                }
            }

            appController.rimettiInCommercio(idLotto, nuovoPrezzo, scontoAttivo, prezzoScontato);
            mostraSuccesso("Lotto " + idLotto + " rimesso in commercio con successo.");

            if (vistaCorrente == StatoVista.RITIRATI) {
                List<LottoBean> lottiRitirati = appController.getLottiRitirati();
                tabellaLotti.setItems(FXCollections.observableArrayList(lottiRitirati));
            }
            pulisciFormLotto();

        } catch (NumberFormatException _) {
            mostraErrore("Inserisci un valore numerico valido per i prezzi prima di rimettere in commercio.");
        } catch (Exception e) {
            mostraErrore("Errore durante il ripristino: " + e.getMessage());
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
        if (vistaCorrente == StatoVista.SCONTI) {
            impostaVistaClassica();
        } else {
            impostaVistaSconti();
        }
    }

    @FXML
    public void vistaDaPrezzareAction() {
        messaggioLabel.setVisible(false);
        if (vistaCorrente == StatoVista.DA_PREZZARE) {
            impostaVistaClassica();
        } else {
            impostaVistaDaPrezzare();
        }
    }

    @FXML
    public void vistaRitiratiAction() {
        messaggioLabel.setVisible(false);
        if (vistaCorrente == StatoVista.RITIRATI) {
            impostaVistaClassica();
        } else {
            impostaVistaRitirati();
        }
    }

    private void impostaVistaClassica() {
        sezioneSuperiore.setVisible(true);
        sezioneSuperiore.setManaged(true);
        btnToggleVista.setText("Prodotti da scontare");
        if (btnDaPrezzare != null) btnDaPrezzare.setText("Prodotti da prezzare");
        if (btnRitirati != null) btnRitirati.setText("Lotti Ritirati");
        vistaCorrente = StatoVista.CLASSICA;

        if (btnRimettiInCommercio != null) { btnRimettiInCommercio.setVisible(false); btnRimettiInCommercio.setManaged(false); }
        if (btnRimuoviLotto != null) { btnRimuoviLotto.setVisible(true); btnRimuoviLotto.setManaged(true); }

        pulisciFormLotto();
        caricaCatalogo();
    }

    private void impostaVistaSconti() {
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
            if (btnDaPrezzare != null) btnDaPrezzare.setText("Prodotti da prezzare");
            if (btnRitirati != null) btnRitirati.setText("Lotti Ritirati");

            if (btnRimettiInCommercio != null) { btnRimettiInCommercio.setVisible(false); btnRimettiInCommercio.setManaged(false); }
            if (btnRimuoviLotto != null) { btnRimuoviLotto.setVisible(true); btnRimuoviLotto.setManaged(true); }

            // Deselezioniamo il prodotto corrente per indicare che stiamo vedendo una vista globale
            tabellaProdotti.getSelectionModel().clearSelection();
            prodottoSelezionatoField.clear();
            nuovaCategoriaComboBox.setValue(null);

            pulisciFormLotto();
            vistaCorrente = StatoVista.SCONTI;

        } catch (Exception e) {
            mostraErrore("Errore durante il recupero dei lotti: " + e.getMessage());
        }
    }

    private void impostaVistaDaPrezzare() {
        try {
            List<LottoBean> lottiDaPrezzare = appController.getLottiDaPrezzare();
            if (lottiDaPrezzare.isEmpty()) {
                mostraSuccesso("Nessun prodotto da prezzare al momento.");
            } else {
                mostraSuccesso("Trovati " + lottiDaPrezzare.size() + " lotti da prezzare.");
            }

            ObservableList<LottoBean> observableList = FXCollections.observableArrayList(lottiDaPrezzare);
            tabellaLotti.setItems(observableList);

            sezioneSuperiore.setVisible(false);
            sezioneSuperiore.setManaged(false);
            if (btnDaPrezzare != null) btnDaPrezzare.setText("Vista classica");
            btnToggleVista.setText("Prodotti da scontare");
            if (btnRitirati != null) btnRitirati.setText("Lotti Ritirati");

            if (btnRimettiInCommercio != null) { btnRimettiInCommercio.setVisible(false); btnRimettiInCommercio.setManaged(false); }
            if (btnRimuoviLotto != null) { btnRimuoviLotto.setVisible(true); btnRimuoviLotto.setManaged(true); }

            tabellaProdotti.getSelectionModel().clearSelection();
            prodottoSelezionatoField.clear();
            nuovaCategoriaComboBox.setValue(null);

            pulisciFormLotto();
            vistaCorrente = StatoVista.DA_PREZZARE;

        } catch (Exception e) {
            mostraErrore("Errore durante il recupero dei lotti da prezzare: " + e.getMessage());
        }
    }

    private void impostaVistaRitirati() {
        try {
            List<LottoBean> lottiRitirati = appController.getLottiRitirati();
            if (lottiRitirati.isEmpty()) {
                mostraSuccesso("Nessun lotto ritirato presente.");
            } else {
                mostraSuccesso("Trovati " + lottiRitirati.size() + " lotti ritirati.");
            }

            ObservableList<LottoBean> observableList = FXCollections.observableArrayList(lottiRitirati);
            tabellaLotti.setItems(observableList);

            sezioneSuperiore.setVisible(false);
            sezioneSuperiore.setManaged(false);
            if (btnRitirati != null) btnRitirati.setText("Vista classica");
            btnToggleVista.setText("Prodotti da scontare");
            if (btnDaPrezzare != null) btnDaPrezzare.setText("Prodotti da prezzare");

            if (btnRimettiInCommercio != null) { btnRimettiInCommercio.setVisible(true); btnRimettiInCommercio.setManaged(true); }
            if (btnRimuoviLotto != null) { btnRimuoviLotto.setVisible(false); btnRimuoviLotto.setManaged(false); }

            tabellaProdotti.getSelectionModel().clearSelection();
            prodottoSelezionatoField.clear();
            nuovaCategoriaComboBox.setValue(null);

            pulisciFormLotto();
            vistaCorrente = StatoVista.RITIRATI;

        } catch (Exception e) {
            mostraErrore("Errore durante il recupero dei lotti ritirati: " + e.getMessage());
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
