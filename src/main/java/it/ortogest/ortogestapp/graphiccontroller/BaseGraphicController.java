package it.ortogest.ortogestapp.graphiccontroller;

import it.ortogest.ortogestapp.utils.Printer;
import it.ortogest.ortogestapp.utils.CostantiGUI;
import it.ortogest.ortogestapp.utils.SceneManager;
import it.ortogest.ortogestapp.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;


public abstract class BaseGraphicController {

    @FXML
    public void logoutAction() {
        
        SessionManager.getInstance().logout();
        Printer.printf("Logout effettuato con successo. Ritorno alla schermata iniziale.");

        cambiaScenaSicuro(CostantiGUI.VIEW_LOGIN,
                "Errore critico durante il logout: impossibile ricaricare Login.fxml.");
    }

    
    protected void cambiaScenaSicuro(String viewName, String errorMessage) {
        try {
            SceneManager.getInstance().cambiaScena(viewName);
        } catch (it.ortogest.ortogestapp.exception.ViewException e) {
            Printer.perror(errorMessage + " " + e.getMessage());
        }
    }

    
    protected void mostraStatusLabel(Label label, String messaggio, boolean successo) {
        if (label != null) {
            label.setText(messaggio);
            label.setTextFill(successo ? Color.web("#27ae60") : Color.web("#e74c3c"));
            label.setVisible(true);
        }
    }

    
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

    
    protected void fissaTabelle(javafx.scene.control.TableView<?>... tables) {
        for (javafx.scene.control.TableView<?> table : tables) {
            if (table != null) {
                table.getColumns().forEach(c -> c.setResizable(true));
                table.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
            }
        }
    }
}
