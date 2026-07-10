package it.ortogest.ortogestapp.graphiccontroller;

import it.ortogest.ortogestapp.appcontroller.CreaOrdineAppController;
import it.ortogest.ortogestapp.beans.RigaOrdineBean;
import it.ortogest.ortogestapp.beans.UtenteBean;
import it.ortogest.ortogestapp.utils.Printer;
import it.ortogest.ortogestapp.utils.CostantiGUI;
import it.ortogest.ortogestapp.utils.SceneManager;
import it.ortogest.ortogestapp.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class RiepilogoOrdineGraphicController extends BaseGraphicController {

    @FXML
    private TableView<RigaOrdineBean> recapTable;

    @FXML
    private TableColumn<RigaOrdineBean, Void> colRimuovi;

    @FXML
    private TableColumn<RigaOrdineBean, Number> colPrezzoUnitario;

    @FXML
    private TableColumn<RigaOrdineBean, Number> colSubtotale;

    @FXML
    private Label recapTotaleLabel;

    @FXML
    private Label statusLabel;

    private List<RigaOrdineBean> carrello;
    private CreaOrdineAppController appController;

    @FXML
    public void initialize() {
        fissaTabelle(recapTable);
        appController = new CreaOrdineAppController();
        carrello = SessionManager.getInstance().getCarrelloCorrente();

        if (carrello == null || carrello.isEmpty()) {
            Printer.perror("Errore: Carrello vuoto in fase di riepilogo.");
            tornaAlCatalogo();
            return;
        }

        setupTabellaRecap();
        rinfrescaTabella();
    }

    private void setupTabellaRecap() {
        setupColonnaPrezzoUnitario();
        setupColonnaSubtotale();
        setupColonnaRimuovi();
    }

    private void setupColonnaPrezzoUnitario() {
        if (colPrezzoUnitario != null) {
            colPrezzoUnitario.setCellFactory(tc -> new TableCell<RigaOrdineBean, Number>() {
                @Override
                protected void updateItem(Number prezzo, boolean empty) {
                    super.updateItem(prezzo, empty);
                    if (empty || prezzo == null) {
                        setText(null);
                    } else {
                        setText(String.format("%.2f", prezzo.doubleValue()));
                    }
                }
            });
        }
    }

    private void setupColonnaSubtotale() {
        if (colSubtotale != null) {
            colSubtotale.setCellFactory(tc -> new TableCell<RigaOrdineBean, Number>() {
                @Override
                protected void updateItem(Number subTotale, boolean empty) {
                    super.updateItem(subTotale, empty);
                    if (empty || subTotale == null) {
                        setText(null);
                    } else {
                        setText(String.format("%.2f", subTotale.doubleValue()));
                    }
                }
            });
        }
    }

    private void setupColonnaRimuovi() {
        colRimuovi.setCellFactory(param -> new TableCell<RigaOrdineBean, Void>() {
            private final Button btn = new Button("Rimuovi");
            {
                btn.getStyleClass().add("btn-danger");
                btn.setOnAction(event -> {
                    RigaOrdineBean riga = getTableView().getItems().get(getIndex());
                    rimuoviDalCarrello(riga);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });
    }

    private void rimuoviDalCarrello(RigaOrdineBean riga) {
        carrello.remove(riga);
        rinfrescaTabella();

        if (carrello.isEmpty()) {
            tornaAlCatalogo();
        }
    }

    private void rinfrescaTabella() {
        ObservableList<RigaOrdineBean> data = FXCollections.observableArrayList(carrello);
        recapTable.setItems(data);

        double totale = 0;
        for (RigaOrdineBean r : carrello) {
            totale += r.getSubtotale();
        }
        recapTotaleLabel.setText(String.format("%.2f EUR", totale));
    }

    @FXML
    public void annullaAction() {
        tornaAlCatalogo();
    }

    @FXML
    public void inviaOrdineAction() {
        UtenteBean currentUser = SessionManager.getInstance().getCurrentUser();
        try {
            String risultato = appController.creaOrdine(currentUser.getEmail(), carrello);

            carrello.clear();
            SessionManager.getInstance().setCarrelloCorrente(null);

            Printer.printf("Ordine confermato: " + risultato);
            tornaAlCatalogo();

        } catch (Exception e) {
            mostraErrore("Errore nell'invio dell'ordine: " + e.getMessage());
            Printer.perror("Errore conferma ordine: " + e.getMessage());
        }
    }

    private void tornaAlCatalogo() {
        try {
            SceneManager.getInstance().cambiaScena(CostantiGUI.VIEW_CLIENTE);
        } catch (it.ortogest.ortogestapp.exception.ViewException e) {
            Printer.perror("Errore critico nel ritorno al catalogo: " + e.getMessage());
        }
    }

    private void mostraErrore(String msg) {
        statusLabel.setText(msg);
        statusLabel.getStyleClass().add("text-error");
        statusLabel.setVisible(true);
    }
}
