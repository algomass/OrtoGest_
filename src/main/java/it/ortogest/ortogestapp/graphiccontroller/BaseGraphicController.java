package it.ortogest.ortogestapp.graphiccontroller;

import it.ortogest.ortogestapp.utils.Printer;
import it.ortogest.ortogestapp.utils.CostantiGUI;
import it.ortogest.ortogestapp.utils.SceneManager;
import it.ortogest.ortogestapp.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.io.IOException;

/**
 * Controller Grafico Base (Boundary).
 * Contiene i metodi e i comportamenti comuni a tutte le schermate, 
 * come ad esempio l'azione di logout (Esci).
 */
public abstract class BaseGraphicController {

    @FXML
    public void logoutAction() {
        // 1. Pulisco la sessione globale rimuovendo l'utente corrente
        SessionManager.getInstance().logout();
        Printer.printf("Logout effettuato con successo. Ritorno alla schermata iniziale.");

        cambiaScenaSicuro(CostantiGUI.VIEW_LOGIN, "Errore critico durante il logout: impossibile ricaricare Login.fxml.");
    }

    /**
     * Esegue il cambio di scena gestendo l'eccezione in modo sicuro.
     * @param viewName Nome della vista da caricare.
     * @param errorMessage Messaggio di errore se fallisce.
     */
    protected void cambiaScenaSicuro(String viewName, String errorMessage) {
        try {
            SceneManager.getInstance().cambiaScena(viewName);
        } catch (IOException e) {
            Printer.perror(errorMessage + " " + e.getMessage());
        }
    }

    /**
     * Centralizza la logica di colorazione delle label di stato (verde se successo, rosso se errore).
     */
    protected void mostraStatusLabel(Label label, String messaggio, boolean successo) {
        if (label != null) {
            label.setText(messaggio);
            label.setTextFill(successo ? Color.web("#27ae60") : Color.web("#e74c3c"));
            label.setVisible(true);
        }
    }

    /**
     * Formats a table column containing numbers to display with exactly two decimal places.
     */
    protected <T, N extends Number> void formatDoubleColumn(javafx.scene.control.TableColumn<T, N> column) {
        if (column != null) {
            column.setCellFactory(tc -> new javafx.scene.control.TableCell<T, N>() {
                @Override
                protected void updateItem(N item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(String.format(java.util.Locale.US, "%.2f", item.doubleValue()));
                    }
                }
            });
        }
    }
}
