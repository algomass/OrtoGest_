package it.ortogest.ortogestapp.graphiccontroller;

import it.ortogest.ortogestapp.appcontroller.GestioneOrdiniAppController;
import it.ortogest.ortogestapp.beans.OrdineBean;
import it.ortogest.ortogestapp.beans.ProdottoBean;
import it.ortogest.ortogestapp.beans.RigaOrdineBean;
import it.ortogest.ortogestapp.beans.UtenteBean;
import it.ortogest.ortogestapp.model.CategoriaProdotto;
import it.ortogest.ortogestapp.utils.Printer;
import it.ortogest.ortogestapp.utils.CostantiGUI;
import it.ortogest.ortogestapp.utils.SceneManager;
import it.ortogest.ortogestapp.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller Grafico (Boundary) per la schermata dell'Area Cliente.
 * Gestisce il catalogo, il carrello multi-prodotto e lo storico ordini.
 */
public class ClienteGraphicController extends BaseGraphicController {

    @FXML
    private FlowPane flowPaneProdotti;

    @FXML
    private ScrollPane scrollPaneCatalogo;

    @FXML
    private VBox ordiniContainer;

    @FXML
    private TableView<OrdineBean> ordiniTable;

    @FXML
    private Label errorLabel;

    @FXML
    private Label titoloSezione;

    @FXML
    private TextField searchField;

    @FXML
    private Button btnFrutta;

    @FXML
    private Button btnVerdura;

    @FXML
    private Button btnOrdini;

    // --- Elementi UI Carrello nell'Header ---
    @FXML
    private Label cartSummaryLabel;

    @FXML
    private Button btnConfermaOrdine;

    @FXML
    private Button btnSvuotaCarrello;

    private GestioneOrdiniAppController appController;

    // Lista locale per gestire il carrello prima dell'invio finale
    private List<RigaOrdineBean> carrello = new ArrayList<>();

    // Stili per i pulsanti della sidebar
    private static final String STILE_BTN_ATTIVO = "-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-size: 14; -fx-alignment: CENTER_LEFT; -fx-padding: 0 0 0 20; -fx-cursor: hand;";
    private static final String STILE_BTN_INATTIVO = "-fx-background-color: transparent; -fx-text-fill: #bdc3c7; -fx-font-size: 14; -fx-alignment: CENTER_LEFT; -fx-padding: 0 0 0 20; -fx-cursor: hand;";

    @FXML
    public void initialize() {
        appController = new GestioneOrdiniAppController();

        // Recupera il carrello dalla sessione se presente (es. ritorno da riepilogo)
        List<RigaOrdineBean> cartInSession = SessionManager.getInstance().getCarrelloCorrente();
        if (cartInSession != null) {
            this.carrello = cartInSession;
        }

        // Di default mostra la categoria Frutta
        mostraFruttaAction();
        aggiornaUIHeaderCarrello();
    }

    // ==================== AZIONI SIDEBAR ====================

    @FXML
    public void mostraFruttaAction() {
        if (searchField != null) searchField.clear();
        aggiornaStileSidebar(btnFrutta);
        titoloSezione.setText("Catalogo — Frutta");
        mostraCatalogo();
        caricaProdottiPerCategoria(CategoriaProdotto.FRUTTA);
    }

    @FXML
    public void mostraVerduraAction() {
        if (searchField != null) searchField.clear();
        aggiornaStileSidebar(btnVerdura);
        titoloSezione.setText("Catalogo — Verdura");
        mostraCatalogo();
        caricaProdottiPerCategoria(CategoriaProdotto.VERDURA);
    }

    @FXML
    public void mostraOrdiniAction() {
        if (searchField != null) searchField.clear();
        aggiornaStileSidebar(btnOrdini);
        titoloSezione.setText("I Miei Ordini");
        mostraOrdini();
        caricaOrdiniCliente();
    }

    // ==================== LOGICA DI VISUALIZZAZIONE E RICERCA ====================

    @FXML
    public void cercaProdottoAction() {
        String cat = btnVerdura.getStyle().contains(STILE_BTN_ATTIVO) ? CategoriaProdotto.VERDURA : CategoriaProdotto.FRUTTA;
        caricaProdottiPerCategoria(cat);
    }

    private void mostraCatalogo() {
        scrollPaneCatalogo.setVisible(true);
        ordiniContainer.setVisible(false);
        errorLabel.setVisible(false);
        aggiornaUIHeaderCarrello();
    }

    private void mostraOrdini() {
        scrollPaneCatalogo.setVisible(false);
        ordiniContainer.setVisible(true);
        errorLabel.setVisible(false);
        aggiornaUIHeaderCarrello();
    }

    private void caricaProdottiPerCategoria(String categoria) {
        List<ProdottoBean> prodotti = appController.getCatalogoPerCategoria(categoria);
        flowPaneProdotti.getChildren().clear();

        String filter = (searchField != null && searchField.getText() != null) ? searchField.getText().trim().toLowerCase() : "";

        boolean trovati = false;
        for (ProdottoBean prodotto : prodotti) {
            if (!filter.isEmpty() && !prodotto.getNome().toLowerCase().contains(filter)) {
                continue;
            }
            trovati = true;
            // Sottrai la quantità già presente nel carrello
            double giacenzaResidua = prodotto.getGiacenza();
            for (RigaOrdineBean r : carrello) {
                if (r.getNomeProdotto().equals(prodotto.getNome())) {
                    giacenzaResidua -= r.getQuantita();
                }
            }
            prodotto.setGiacenza(giacenzaResidua);

            VBox card = creaProdottoCard(prodotto);
            flowPaneProdotti.getChildren().add(card);
        }

        if (!trovati) {
            Label vuoto = new Label(filter.isEmpty() ? "Nessun prodotto disponibile in questa categoria." : "Nessun prodotto corrisponde alla ricerca.");
            vuoto.setTextFill(Color.web("#7f8c8d"));
            vuoto.setStyle("-fx-font-size: 14;");
            flowPaneProdotti.getChildren().add(vuoto);
        }
    }

    private void caricaOrdiniCliente() {
        UtenteBean currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            List<OrdineBean> ordini = appController.getOrdiniCliente(currentUser.getEmail());
            ObservableList<OrdineBean> data = FXCollections.observableArrayList(ordini);
            ordiniTable.setItems(data);
        }
    }

    @FXML
    public void rimuoviOrdineAction() {
        OrdineBean selected = ordiniTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            mostraMessaggio("Seleziona un ordine da rimuovere.", false);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Sei sicuro di voler rimuovere l'ordine " + selected.getIdOrdine() + "?\nLe quantità verranno ripristinate in magazzino.", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            try {
                appController.eliminaOrdine(selected.getIdOrdine());
                mostraMessaggio("Ordine rimosso con successo.", true);
                caricaOrdiniCliente();
            } catch (it.ortogest.ortogestapp.exception.GestioneException e) {
                mostraMessaggio("Errore nella rimozione: " + e.getMessage(), false);
            }
        }
    }

    // ==================== GESTIONE CARRELLO ====================

    /**
     * Mostra il popup per selezionare un lotto da cui acquistare.
     */
    private void mostraPopupSelezioneLotto(ProdottoBean prodotto) {
        errorLabel.setVisible(false);
        
        List<it.ortogest.ortogestapp.model.Lotto> lotti = appController.getLottiDisponibili(prodotto.getNome());
        if (lotti == null || lotti.isEmpty()) {
            mostraMessaggio("Nessun lotto disponibile per questo prodotto.", false);
            return;
        }

        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Scegli un lotto per " + prodotto.getNome());
        
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);
        
        Label title = new Label("Lotti disponibili per " + prodotto.getNome());
        title.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
        root.getChildren().add(title);
        
        for (it.ortogest.ortogestapp.model.Lotto l : lotti) {
            // Calcola disponibilità residua del lotto rispetto al carrello
            double giacenzaResidua = l.getQuantitaKg();
            for (RigaOrdineBean r : carrello) {
                if (l.getIdLotto().equals(r.getIdLotto())) {
                    giacenzaResidua -= r.getQuantita();
                }
            }
            
            if (giacenzaResidua <= 0) continue;
            
            HBox lottoBox = new HBox(15);
            lottoBox.setAlignment(Pos.CENTER_LEFT);
            lottoBox.setStyle("-fx-border-color: #bdc3c7; -fx-border-radius: 5; -fx-padding: 10;");
            
            VBox infoBox = new VBox(5);
            Label scadeLabel = new Label("Scadenza: " + l.getDataScadenza());
            scadeLabel.setStyle("-fx-font-weight: bold;");
            
            double prezzoReale = (l.isScontoScadenzaAttivo() && l.getPrezzoScontato() > 0) ? l.getPrezzoScontato() : l.getPrezzoVendita();
            Label prezzoLabel = new Label(String.format("Prezzo: %.2f €/Kg", prezzoReale));
            if (l.isScontoScadenzaAttivo() && l.getPrezzoScontato() > 0) {
                prezzoLabel.setTextFill(Color.web("#e74c3c"));
            }
            
            Label qtaLabel = new Label(String.format("Disp: %.1f Kg", giacenzaResidua));
            infoBox.getChildren().addAll(scadeLabel, prezzoLabel, qtaLabel);
            
            TextField quantitaField = new TextField();
            quantitaField.setPromptText("Kg");
            quantitaField.setPrefWidth(60);
            
            Button aggiungiBtn = new Button("Aggiungi");
            aggiungiBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand;");
            
            double finalGiacenzaResidua = giacenzaResidua;
            aggiungiBtn.setOnAction(e -> {
                aggiungiLottoAlCarrello(l.getIdLotto(), prodotto.getNome(), quantitaField.getText(), prezzoReale, finalGiacenzaResidua, popupStage);
            });
            
            lottoBox.getChildren().addAll(infoBox, quantitaField, aggiungiBtn);
            root.getChildren().add(lottoBox);
        }
        
        if (root.getChildren().size() == 1) {
            Label noLotti = new Label("Nessun lotto disponibile o quantità già nel carrello.");
            root.getChildren().add(noLotti);
        }
        
        Scene scene = new Scene(root, 400, 400);
        popupStage.setScene(scene);
        popupStage.show();
    }

    private void aggiungiLottoAlCarrello(String idLotto, String nomeProdotto, String qtaStr, double prezzoUnitario, double maxAcquistabile, Stage popupStage) {
        if (qtaStr == null || qtaStr.trim().isEmpty()) {
            mostraMessaggio("Inserisci la quantità.", false);
            return;
        }

        double qta;
        try {
            qta = Double.parseDouble(qtaStr);
            if (qta <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            mostraMessaggio("Quantità non valida (deve essere > 0).", false);
            return;
        }

        if (qta > maxAcquistabile) {
            mostraMessaggio("Quantità superiore alla disponibilità del lotto (" + maxAcquistabile + " Kg).", false);
            return;
        }

        // Aggiunge o aggiorna nel carrello
        boolean trovato = false;
        for (RigaOrdineBean r : carrello) {
            if (r.getIdLotto() != null && r.getIdLotto().equals(idLotto)) {
                r.setQuantita(r.getQuantita() + qta);
                trovato = true;
                break;
            }
        }

        if (!trovato) {
            carrello.add(new RigaOrdineBean(nomeProdotto, idLotto, qta, prezzoUnitario));
        }

        popupStage.close();
        mostraMessaggio(nomeProdotto + " aggiunto al carrello!", true);
        aggiornaUIHeaderCarrello();

        // Ricarica la categoria per aggiornare le giacenze sulle card
        String cat = btnVerdura.getStyle().contains(STILE_BTN_ATTIVO) ? CategoriaProdotto.VERDURA : CategoriaProdotto.FRUTTA;
        caricaProdottiPerCategoria(cat);
    }

    /**
     * Salva il carrello in sessione e passa alla schermata di riepilogo dedicata.
     */
    @FXML
    public void confermaOrdineAction() {
        if (carrello.isEmpty())
            return;

        try {
            // Salva il carrello nel SessionManager per passarlo alla nuova scena
            SessionManager.getInstance().setCarrelloCorrente(carrello);

            // Cambia scena verso il file FXML dedicato
            SceneManager.getInstance().cambiaScena(CostantiGUI.VIEW_RIEPILOGO_ORDINE);

        } catch (IOException e) {
            mostraMessaggio("Errore nel caricamento del riepilogo: " + e.getMessage(), false);
            Printer.perror("Errore cambio scena riepilogo: " + e.getMessage());
        }
    }

    @FXML
    public void svuotaCarrelloAction() {
        carrello.clear();
        aggiornaUIHeaderCarrello();
        mostraMessaggio("Carrello svuotato.", true);

        // Ricarica la categoria per ripristinare le giacenze originali sulle card
        String cat = btnVerdura.getStyle().contains(STILE_BTN_ATTIVO) ? CategoriaProdotto.VERDURA : CategoriaProdotto.FRUTTA;
        caricaProdottiPerCategoria(cat);
    }

    private void aggiornaUIHeaderCarrello() {
        if (carrello.isEmpty()) {
            cartSummaryLabel.setText("Carrello Vuoto");
            btnConfermaOrdine.setVisible(false);
            btnSvuotaCarrello.setVisible(false);
        } else {
            double totale = 0;
            int pezzi = 0;
            for (RigaOrdineBean r : carrello) {
                totale += r.getSubtotale();
                pezzi++;
            }
            cartSummaryLabel.setText(String.format("%d prod. — Tot: %.2f €", pezzi, totale));
            btnConfermaOrdine.setVisible(true);
            btnSvuotaCarrello.setVisible(true);
        }
    }

    // ==================== COSTRUZIONE CARD ====================

    private VBox creaProdottoCard(ProdottoBean prodotto) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(180);
        card.setPadding(new Insets(15));
        card.setStyle(
                "-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #bdc3c7; -fx-border-radius: 8;");

        ImageView imageView = new ImageView();
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);

        try {
            InputStream imgStream = getClass().getResourceAsStream(prodotto.getImmaginePath());
            if (imgStream != null)
                imageView.setImage(new Image(imgStream));
        } catch (Exception e) {
            Printer.perror("Errore caricamento immagine: " + e.getMessage());
        }

        Label nomeLabel = new Label(prodotto.getNome());
        nomeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        VBox prezziBox = new VBox(2);
        prezziBox.setAlignment(Pos.CENTER);
        
        if (prodotto.getPrezzoMin() == prodotto.getPrezzoMax()) {
            Label prezzoLabel = new Label(String.format("%.2f €/Kg", prodotto.getPrezzoMin()));
            prezzoLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 14;");
            prezziBox.getChildren().add(prezzoLabel);
        } else {
            Label badgeVari = new Label("Lotti Multipli");
            badgeVari.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-padding: 3 6; -fx-background-radius: 4; -fx-font-size: 10; -fx-font-weight: bold;");
            
            Label prezzoLabel = new Label(String.format("Da %.2f € a %.2f €", prodotto.getPrezzoMin(), prodotto.getPrezzoMax()));
            prezzoLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 13;");
            prezziBox.getChildren().addAll(badgeVari, prezzoLabel);
        }

        Label giacenzaLabel = new Label(String.format("Disp. Totale: %.1f Kg", prodotto.getGiacenza()));
        giacenzaLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #7f8c8d;");

        Button selezionaBtn = new Button("Scegli Lotto");
        selezionaBtn.setStyle(
                "-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        selezionaBtn.setOnAction(e -> mostraPopupSelezioneLotto(prodotto));

        HBox bottomBox = new HBox(5, selezionaBtn);
        bottomBox.setAlignment(Pos.CENTER);

        card.getChildren().addAll(imageView, nomeLabel, prezziBox, giacenzaLabel, bottomBox);

        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: #f8f9fa; -fx-background-radius: 8; -fx-border-color: #3498db; -fx-border-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);"));
        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #bdc3c7; -fx-border-radius: 8;"));

        return card;
    }

    // ==================== UTILITÀ ====================

    private void aggiornaStileSidebar(Button attivo) {
        btnFrutta.setStyle(STILE_BTN_INATTIVO);
        btnVerdura.setStyle(STILE_BTN_INATTIVO);
        btnOrdini.setStyle(STILE_BTN_INATTIVO);
        attivo.setStyle(STILE_BTN_ATTIVO);
    }

    private void mostraMessaggio(String msg, boolean successo) {
        errorLabel.setText(msg);
        errorLabel.setTextFill(successo ? Color.web("#27ae60") : Color.web("#e74c3c"));
        errorLabel.setVisible(true);
    }
}
