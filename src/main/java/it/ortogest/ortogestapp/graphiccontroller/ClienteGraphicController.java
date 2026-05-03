package it.ortogest.ortogestapp.graphiccontroller;

import it.ortogest.ortogestapp.appcontroller.GestioneOrdiniAppController;
import it.ortogest.ortogestapp.beans.OrdineBean;
import it.ortogest.ortogestapp.beans.ProdottoBean;
import it.ortogest.ortogestapp.beans.RigaOrdineBean;
import it.ortogest.ortogestapp.beans.UtenteBean;
import it.ortogest.ortogestapp.model.CategoriaProdotto;
import it.ortogest.ortogestapp.utils.Printer;
import it.ortogest.ortogestapp.utils.SceneManager;
import it.ortogest.ortogestapp.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

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
        aggiornaStileSidebar(btnFrutta);
        titoloSezione.setText("Catalogo — Frutta");
        mostraCatalogo();
        caricaProdottiPerCategoria(CategoriaProdotto.FRUTTA);
    }

    @FXML
    public void mostraVerduraAction() {
        aggiornaStileSidebar(btnVerdura);
        titoloSezione.setText("Catalogo — Verdura");
        mostraCatalogo();
        caricaProdottiPerCategoria(CategoriaProdotto.VERDURA);
    }

    @FXML
    public void mostraOrdiniAction() {
        aggiornaStileSidebar(btnOrdini);
        titoloSezione.setText("I Miei Ordini");
        mostraOrdini();
        caricaOrdiniCliente();
    }

    // ==================== LOGICA DI VISUALIZZAZIONE ====================

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

        if (prodotti.isEmpty()) {
            Label vuoto = new Label("Nessun prodotto disponibile in questa categoria.");
            vuoto.setTextFill(Color.web("#7f8c8d"));
            vuoto.setStyle("-fx-font-size: 14;");
            flowPaneProdotti.getChildren().add(vuoto);
            return;
        }

        for (ProdottoBean prodotto : prodotti) {
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
    }

    private void caricaOrdiniCliente() {
        UtenteBean currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            List<OrdineBean> ordini = appController.getOrdiniCliente(currentUser.getEmail());
            ObservableList<OrdineBean> data = FXCollections.observableArrayList(ordini);
            ordiniTable.setItems(data);
        }
    }

    // ==================== GESTIONE CARRELLO ====================

    /**
     * Aggiunge un prodotto al carrello locale.
     */
    private void aggiungiAlCarrelloAction(ProdottoBean prodotto, TextField quantitaField) {
        errorLabel.setVisible(false);

        String qtaStr = quantitaField.getText();
        if (qtaStr == null || qtaStr.trim().isEmpty()) {
            mostraMessaggio("Inserisci la quantità per " + prodotto.getNome() + ".", false);
            return;
        }

        double qta;
        try {
            qta = Double.parseDouble(qtaStr);
            if (qta <= 0)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            mostraMessaggio("Quantità non valida (deve essere > 0).", false);
            return;
        }

        if (qta > prodotto.getGiacenza()) {
            mostraMessaggio("Quantità superiore alla disponibilità (" + prodotto.getGiacenza() + " Kg).", false);
            return;
        }

        // Verifica se il prodotto è già nel carrello, nel caso aggiorna la quantità
        boolean trovato = false;
        for (RigaOrdineBean r : carrello) {
            if (r.getNomeProdotto().equals(prodotto.getNome())) {
                r.setQuantita(r.getQuantita() + qta);
                trovato = true;
                break;
            }
        }

        if (!trovato) {
            carrello.add(new RigaOrdineBean(prodotto.getNome(), qta, prodotto.getPrezzoAttuale()));
        }

        quantitaField.clear();
        mostraMessaggio(prodotto.getNome() + " aggiunto al carrello!", true);
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
            SceneManager.getInstance().cambiaScena("/GUI/RiepilogoOrdine.fxml");

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

        Label prezzoLabel = new Label(String.format("%.2f €/Kg", prodotto.getPrezzoAttuale()));
        prezzoLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");

        Label giacenzaLabel = new Label(String.format("Disp: %.1f Kg", prodotto.getGiacenza()));
        giacenzaLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #7f8c8d;");

        TextField quantitaField = new TextField();
        quantitaField.setPromptText("Kg");
        quantitaField.setPrefWidth(60);
        quantitaField.setAlignment(Pos.CENTER);

        Button aggiungiBtn = new Button("Aggiungi");
        aggiungiBtn.setStyle(
                "-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        aggiungiBtn.setOnAction(e -> aggiungiAlCarrelloAction(prodotto, quantitaField));

        HBox bottomBox = new HBox(5, quantitaField, aggiungiBtn);
        bottomBox.setAlignment(Pos.CENTER);

        card.getChildren().addAll(imageView, nomeLabel, prezzoLabel, giacenzaLabel, bottomBox);

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
