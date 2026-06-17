package it.ortogest.ortogestapp.graphiccontroller;

import java.io.IOException;

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

/**
 * LoginGraphicController gestisce le interazioni dell'utente nella vista di
 * Login.
 * Implementa un design pulito ed a basso accoppiamento:
 * - Delega la persistenza dello stato (utente) al SessionManager.
 * - Delega la navigazione (cambio scena) allo SceneManager.
 * In questo modo il controller ha la singola responsabilità di orchestrare le
 * azioni della GUI.
 */
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
        // Popoliamo la combobox all'avvio della schermata
        ruoloComboBox.getItems().addAll("Cliente", "Magazziniere", "Operatore", "Responsabile");
    }

    @FXML
    public void loginAction() {
        // Nascondiamo eventuali errori precedenti
        errorLabel.setVisible(false);

        // 1. Leggo i dati reali dalla GUI
        String emailInserita = emailField.getText();
        String passwordInserita = passwordField.getText();
        String ruoloSelezionato = ruoloComboBox.getValue();

        // Controllo validazione base (sintattica) prima di inviare i dati
        if (emailInserita == null || emailInserita.trim().isEmpty() ||
                passwordInserita == null || passwordInserita.trim().isEmpty()) {
            mostraErrore("Compila tutti i campi!");
            return;
        }

        // 2. Impacchetto i dati nel Bean e chiamo il Controller Applicativo
        CredenzialiBean credenziali = new CredenzialiBean(emailInserita, passwordInserita);
        LoginAppController appController = new LoginAppController();

        try {
            // L'AppController esegue la logica (tramite il DAO) e ci restituisce il Bean
            // dell'utente
            UtenteBean utenteLoggato = appController.login(credenziali);

            // Per ora il ruolo lo forziamo se non l'ha scelto, nella realtà potrebbe essere
            // un controllo extra
            if (ruoloSelezionato != null) {
                utenteLoggato.setRuolo(ruoloSelezionato);
            }

            // 3. Memorizzo l'utente nella sessione globale
            SessionManager.getInstance().login(utenteLoggato);
            Printer.printf("Login effettuato con successo. Benvenuto, " + utenteLoggato.getNome() + "!");

            // 4. Richiedo allo SceneManager di cambiare la vista in base al ruolo
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
                    fxmlPath = CostantiGUI.VIEW_MAGAZZINO; // Fallback
                    Printer.perror("Attenzione: ruolo non riconosciuto (" + utenteLoggato.getRuolo() + ").");
                    break;
            }
            SceneManager.getInstance().cambiaScena(fxmlPath);

        } catch (it.ortogest.ortogestapp.exception.LoginFallitoException e) {
            // Se fallisce, mostriamo l'errore sulla Label rossa e sulla console tramite
            // Printer
            Printer.perror("Errore di accesso: " + e.getMessage());
            mostraErrore(e.getMessage());
        } catch (IOException e) {
            Printer.perror("Errore di caricamento: Impossibile trovare la schermata successiva. " + e.getMessage());
            mostraErrore("Errore di sistema. Contatta l'assistenza.");
        }
    }

    private void mostraErrore(String messaggio) {
        errorLabel.setText(messaggio);
        errorLabel.setVisible(true);
    }
}
