package it.ortogest.ortogestapp.graphiccontroller;

import it.ortogest.ortogestapp.appcontroller.CreaOrdineAppController;
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

import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class ClienteGraphicController extends BaseGraphicController {

    private static final String MENU_BTN_ACTIVE = "menu-btn-active";

    @FXML
    private FlowPane flowPaneProdotti;

    @FXML
    private ScrollPane scrollPaneCatalogo;

    @FXML
    private TableView<ProdottoBean> catalogoTable;

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

    @FXML
    private Label cartSummaryLabel;

    @FXML
    private Button btnConfermaOrdine;

    @FXML
    private Button btnSvuotaCarrello;

    private CreaOrdineAppController appController;

    private List<RigaOrdineBean> carrello = new ArrayList<>();

    @FXML
    public void initialize() {
        fissaTabelle(ordiniTable);
        appController = CreaOrdineAppController.getInstance();

        List<RigaOrdineBean> cartInSession = SessionManager.getInstance().getCarrelloCorrente();
        if (cartInSession != null) {
            this.carrello = cartInSession;
        }

        mostraFruttaAction();
        aggiornaUIHeaderCarrello();
    }

    @FXML
    public void mostraFruttaAction() {
        if (searchField != null)
            searchField.clear();
        aggiornaStileSidebar(btnFrutta);
        titoloSezione.setText("Catalogo - Frutta");
        mostraCatalogo();
        caricaProdottiPerCategoria(CategoriaProdotto.FRUTTA);
    }

    @FXML
    public void mostraVerduraAction() {
        if (searchField != null)
            searchField.clear();
        aggiornaStileSidebar(btnVerdura);
        titoloSezione.setText("Catalogo - Verdura");
        mostraCatalogo();
        caricaProdottiPerCategoria(CategoriaProdotto.VERDURA);
    }

    @FXML
    public void mostraOrdiniAction() {
        if (searchField != null)
            searchField.clear();
        aggiornaStileSidebar(btnOrdini);
        titoloSezione.setText("I Miei Ordini");
        mostraOrdini();
        caricaOrdiniCliente();
    }

    @FXML
    public void cercaProdottoAction() {
        String cat = btnVerdura.getStyleClass().contains(MENU_BTN_ACTIVE) ? CategoriaProdotto.VERDURA
                : CategoriaProdotto.FRUTTA;
        caricaProdottiPerCategoria(cat);
    }

    private void mostraCatalogo() {
        scrollPaneCatalogo.setVisible(true);
        ordiniContainer.setVisible(false);
        errorLabel.setVisible(false);
        if (searchField != null)
            searchField.setVisible(true);
        aggiornaUIHeaderCarrello();
    }

    private void mostraOrdini() {
        scrollPaneCatalogo.setVisible(false);
        ordiniContainer.setVisible(true);
        errorLabel.setVisible(false);
        if (searchField != null)
            searchField.setVisible(false);
        aggiornaUIHeaderCarrello();
    }

    private void caricaProdottiPerCategoria(String categoria) {
        List<ProdottoBean> prodotti = appController.getCatalogoPerCategoria(categoria);
        flowPaneProdotti.getChildren().clear();

        String filter = (searchField != null && searchField.getText() != null)
                ? searchField.getText().trim().toLowerCase()
                : "";

        boolean trovati = false;
        for (ProdottoBean prodotto : prodotti) {
            if (!filter.isEmpty() && !prodotto.getNome().toLowerCase().contains(filter)) {
                continue;
            }
            trovati = true;

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
            Label vuoto = new Label(filter.isEmpty() ? "Nessun prodotto disponibile in questa categoria."
                    : "Nessun prodotto corrisponde alla ricerca.");
            vuoto.getStyleClass().add("vuoto-label");
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

        if ("Pronto per il Ritiro".equals(selected.getStato())) {
            Alert errAlert = new Alert(Alert.AlertType.ERROR,
                    "L'ordine è in attesa di ritiro dunque non può essere annullato.", ButtonType.OK);
            errAlert.setHeaderText("Impossibile annullare l'ordine");
            errAlert.showAndWait();
            return;
        }

        String messaggioConferma;
        if ("Ritirato".equals(selected.getStato())) {
            messaggioConferma = "Sei sicuro di voler rimuovere l'ordine " + selected.getIdOrdine() + " dallo storico?";
        } else {
            messaggioConferma = "Sei sicuro di voler rimuovere l'ordine " + selected.getIdOrdine()
                    + "?\nLe quantità verranno ripristinate in magazzino.";
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, messaggioConferma, ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            try {
                appController.eliminaOrdine(selected.getIdOrdine());
                mostraMessaggio("Ordine rimosso con successo.", true);
                caricaOrdiniCliente();
            } catch (it.ortogest.ortogestapp.exception.GestioneException e) {
                Alert errAlert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                errAlert.setHeaderText("Impossibile annullare l'ordine");
                errAlert.showAndWait();
            }
        }
    }

    private void mostraPopupSelezioneLotto(ProdottoBean prodotto) {
        errorLabel.setVisible(false);

        List<it.ortogest.ortogestapp.beans.LottoBean> lotti = appController.getLottiDisponibili(prodotto.getNome());
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
        title.getStyleClass().add("title-bold-16");
        root.getChildren().add(title);

        for (it.ortogest.ortogestapp.beans.LottoBean l : lotti) {

            double giacenzaResidua = calcolaGiacenzaResidua(l);

            if (giacenzaResidua <= 0)
                continue;

            HBox lottoBox = new HBox(15);
            lottoBox.setAlignment(Pos.CENTER_LEFT);
            lottoBox.getStyleClass().add("lotto-box");

            VBox infoBox = new VBox(5);
            Label scadeLabel = new Label("Scadenza: " + l.getDataScadenza());
            scadeLabel.getStyleClass().add("font-bold");

            double prezzoReale = (l.isScontoScadenzaAttivo() && l.getPrezzoScontato() > 0) ? l.getPrezzoScontato()
                    : l.getPrezzoVendita();
            Label prezzoLabel = new Label(String.format("Prezzo: %.2f EUR/Kg", prezzoReale));
            if (l.isScontoScadenzaAttivo() && l.getPrezzoScontato() > 0) {
                prezzoLabel.getStyleClass().add("text-error");
            }

            Label qtaLabel = new Label(String.format("Disp: %.1f Kg", giacenzaResidua));
            infoBox.getChildren().addAll(scadeLabel, prezzoLabel, qtaLabel);

            TextField quantitaField = new TextField();
            quantitaField.setPromptText("Kg");
            quantitaField.setPrefWidth(60);

            Button aggiungiBtn = new Button("Aggiungi");
            aggiungiBtn.getStyleClass().add("btn-success");

            double finalGiacenzaResidua = giacenzaResidua;
            aggiungiBtn.setOnAction(e -> {
                aggiungiLottoAlCarrello(l.getIdLotto(), prodotto.getNome(), quantitaField.getText(), prezzoReale,
                        finalGiacenzaResidua, popupStage);
            });

            lottoBox.getChildren().addAll(infoBox, quantitaField, aggiungiBtn);
            root.getChildren().add(lottoBox);
        }

        if (root.getChildren().size() == 1) {
            Label noLotti = new Label("Nessun lotto disponibile o quantità già nel carrello.");
            root.getChildren().add(noLotti);
        }

        Scene scene = new Scene(root, 400, 400);
        scene.getStylesheets().add(getClass().getResource("/GUI/style.css").toExternalForm());
        popupStage.setScene(scene);
        popupStage.show();
    }

    private double calcolaGiacenzaResidua(it.ortogest.ortogestapp.beans.LottoBean l) {
        double giacenzaResidua = l.getQuantitaKg();
        for (RigaOrdineBean r : carrello) {
            if (r.getIdLotto() != null && l.getIdLotto().equals(r.getIdLotto())) {
                giacenzaResidua -= r.getQuantita();
            }
        }
        return giacenzaResidua;
    }

    private void aggiungiLottoAlCarrello(String idLotto, String nomeProdotto, String qtaStr, double prezzoUnitario,
            double maxAcquistabile, Stage popupStage) {
        if (qtaStr == null || qtaStr.trim().isEmpty()) {
            mostraMessaggio("Inserisci la quantità.", false);
            return;
        }

        double qta;
        try {
            qta = Double.parseDouble(qtaStr);
            if (qta <= 0)
                throw new it.ortogest.ortogestapp.exception.ValidationException(
                        "La quantità deve essere maggiore di zero.");
        } catch (NumberFormatException _) {
            mostraMessaggio("Quantità non valida (deve essere un numero).", false);
            return;
        } catch (it.ortogest.ortogestapp.exception.ValidationException e) {
            mostraMessaggio(e.getMessage(), false);
            return;
        }

        if (qta > maxAcquistabile) {
            mostraMessaggio("Quantità superiore alla disponibilità del lotto (" + maxAcquistabile + " Kg).", false);
            return;
        }

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

        String cat = btnVerdura.getStyleClass().contains(MENU_BTN_ACTIVE) ? CategoriaProdotto.VERDURA
                : CategoriaProdotto.FRUTTA;
        caricaProdottiPerCategoria(cat);
    }

    @FXML
    public void confermaOrdineAction() {
        if (carrello.isEmpty())
            return;

        try {

            SessionManager.getInstance().setCarrelloCorrente(carrello);

            SceneManager.getInstance().cambiaScena(CostantiGUI.VIEW_RIEPILOGO_ORDINE);

        } catch (it.ortogest.ortogestapp.exception.ViewException e) {
            mostraMessaggio("Errore nel caricamento del riepilogo: " + e.getMessage(), false);
            Printer.perror("Errore cambio scena riepilogo: " + e.getMessage());
        }
    }

    @FXML
    public void svuotaCarrelloAction() {
        carrello.clear();
        aggiornaUIHeaderCarrello();
        mostraMessaggio("Carrello svuotato.", true);

        String cat = btnVerdura.getStyleClass().contains(MENU_BTN_ACTIVE) ? CategoriaProdotto.VERDURA
                : CategoriaProdotto.FRUTTA;
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
            cartSummaryLabel.setText(String.format("%d prod. - Tot: %.2f EUR", pezzi, totale));
            btnConfermaOrdine.setVisible(true);
            btnSvuotaCarrello.setVisible(true);
        }
    }

    private VBox creaProdottoCard(ProdottoBean prodotto) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(180);
        card.setPadding(new Insets(15));
        card.getStyleClass().add("product-card");

        // Manteniamo lo spazio dedicato all'immagine come richiesto
        javafx.scene.layout.Region imageSpacer = new javafx.scene.layout.Region();
        imageSpacer.setPrefSize(100, 100);
        imageSpacer.setMinSize(100, 100);
        imageSpacer.setMaxSize(100, 100);

        Label nomeLabel = new Label(prodotto.getNome());
        nomeLabel.getStyleClass().add("title-bold-14");

        VBox prezziBox = new VBox(2);
        prezziBox.setAlignment(Pos.CENTER);

        if (prodotto.getPrezzoMin() == prodotto.getPrezzoMax()) {
            Label prezzoLabel = new Label(String.format("%.2f EUR/Kg", prodotto.getPrezzoMin()));
            prezzoLabel.getStyleClass().add("price-label-big");
            prezziBox.getChildren().add(prezzoLabel);
        } else {
            Label badgeVari = new Label("Lotti Multipli");
            badgeVari.getStyleClass().add("badge-warning");

            Label prezzoLabel = new Label(
                    String.format("Da %.2f EUR a %.2f EUR", prodotto.getPrezzoMin(), prodotto.getPrezzoMax()));
            prezzoLabel.getStyleClass().add("price-label-small");
            prezziBox.getChildren().addAll(badgeVari, prezzoLabel);
        }

        Label giacenzaLabel = new Label(String.format("Disp. Totale: %.1f Kg", prodotto.getGiacenza()));
        giacenzaLabel.getStyleClass().add("giacenza-label");

        Button selezionaBtn = new Button("Scegli Lotto");
        selezionaBtn.getStyleClass().add("btn-primary");
        selezionaBtn.setOnAction(e -> mostraPopupSelezioneLotto(prodotto));

        HBox bottomBox = new HBox(5, selezionaBtn);
        bottomBox.setAlignment(Pos.CENTER);

        card.getChildren().addAll(imageSpacer, nomeLabel, prezziBox, giacenzaLabel, bottomBox);

        return card;
    }

    private void setMenuBtnStyle(Button btn, boolean active) {
        btn.getStyleClass().removeAll(MENU_BTN_ACTIVE, "menu-btn-inactive");
        if (!btn.getStyleClass().contains("menu-btn")) {
            btn.getStyleClass().add("menu-btn");
        }
        btn.getStyleClass().add(active ? MENU_BTN_ACTIVE : "menu-btn-inactive");
    }

    private void aggiornaStileSidebar(Button attivo) {
        setMenuBtnStyle(btnFrutta, false);
        setMenuBtnStyle(btnVerdura, false);
        setMenuBtnStyle(btnOrdini, false);
        setMenuBtnStyle(attivo, true);
    }

    private void mostraMessaggio(String msg, boolean successo) {
        mostraStatusLabel(errorLabel, msg, successo);
    }
}
