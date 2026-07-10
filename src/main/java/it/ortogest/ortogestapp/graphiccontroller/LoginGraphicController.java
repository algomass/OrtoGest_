package it.ortogest.ortogestapp.graphiccontroller;

import it.ortogest.ortogestapp.beans.CredenzialiBean;
import it.ortogest.ortogestapp.beans.UtenteBean;
import it.ortogest.ortogestapp.appcontroller.LoginAppController;
import it.ortogest.ortogestapp.utils.Printer;
import it.ortogest.ortogestapp.utils.SceneManager;
import it.ortogest.ortogestapp.utils.SessionManager;
import it.ortogest.ortogestapp.utils.CostantiGUI;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginGraphicController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> ruoloComboBox;

    @FXML
    private Label errorLabel;

    @FXML
    public void initialize() {

        ruoloComboBox.getItems().addAll("Cliente", "Magazziniere", "Operatore", "Responsabile");
    }

    @FXML
    public void loginAction() {

        errorLabel.setVisible(false);

        String emailInserita = emailField.getText();
        String passwordInserita = passwordField.getText();
        String ruoloSelezionato = ruoloComboBox.getValue();

        if (emailInserita == null || emailInserita.trim().isEmpty() ||
                passwordInserita == null || passwordInserita.trim().isEmpty()) {
            mostraErrore("Compila tutti i campi!");
            return;
        }

        if (ruoloSelezionato == null) {
            mostraErrore("Seleziona un ruolo con cui accedere!");
            return;
        }

        CredenzialiBean credenziali = new CredenzialiBean(emailInserita, passwordInserita);
        LoginAppController appController = new LoginAppController();

        try {

            UtenteBean utenteLoggato = appController.login(credenziali);

            if (!utenteLoggato.getRuolo().equalsIgnoreCase(ruoloSelezionato)) {
                mostraErrore("L'utente non dispone dei permessi \n per accedere come " + ruoloSelezionato + ".");
                return;
            }

            SessionManager.getInstance().login(utenteLoggato);
            Printer.printf("Login effettuato con successo. Benvenuto, " + utenteLoggato.getNome() + "!");

            String fxmlPath;
            switch (utenteLoggato.getRuolo()) {
                case "Magazziniere":
                    fxmlPath = CostantiGUI.VIEW_MAGAZZINO;
                    break;
                case "Responsabile":
                    fxmlPath = CostantiGUI.VIEW_RESPONSABILE;
                    break;
                case "Operatore":
                    fxmlPath = CostantiGUI.VIEW_CASSA;
                    break;
                case "Cliente":
                    fxmlPath = CostantiGUI.VIEW_CLIENTE;
                    break;
                default:
                    fxmlPath = CostantiGUI.VIEW_MAGAZZINO;
                    Printer.perror("Attenzione: ruolo non riconosciuto (" + utenteLoggato.getRuolo() + ").");
                    break;
            }
            SceneManager.getInstance().cambiaScena(fxmlPath);

        } catch (it.ortogest.ortogestapp.exception.LoginFallitoException e) {

            Printer.perror("Errore di accesso: " + e.getMessage());
            mostraErrore(e.getMessage());
        } catch (it.ortogest.ortogestapp.exception.ViewException e) {
            Printer.perror("Errore di caricamento: Impossibile trovare la schermata successiva. " + e.getMessage());
            mostraErrore("Errore di sistema. Contatta l'assistenza.");
        }
    }

    private void mostraErrore(String messaggio) {
        errorLabel.setText(messaggio);
        errorLabel.setVisible(true);
    }
}
