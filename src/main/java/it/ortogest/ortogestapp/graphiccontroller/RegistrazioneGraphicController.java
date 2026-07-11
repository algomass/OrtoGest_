package it.ortogest.ortogestapp.graphiccontroller;

import it.ortogest.ortogestapp.appcontroller.RegistrazioneAppController;
import it.ortogest.ortogestapp.beans.NuovoUtenteBean;
import it.ortogest.ortogestapp.utils.CostantiGUI;
import it.ortogest.ortogestapp.utils.Printer;
import it.ortogest.ortogestapp.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

public class RegistrazioneGraphicController {

    @FXML
    private TextField nomeField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confermaPasswordField;

    @FXML
    private Label messageLabel;

    @FXML
    public void registraAction() {
        messageLabel.setVisible(false);

        String nome = nomeField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confermaPassword = confermaPasswordField.getText();

        if (nome == null || nome.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            confermaPassword == null || confermaPassword.trim().isEmpty()) {
            mostraMessaggio("Compila tutti i campi!", true);
            return;
        }

        if (!password.equals(confermaPassword)) {
            mostraMessaggio("Le password non coincidono!", true);
            return;
        }

        NuovoUtenteBean nuovoUtente = new NuovoUtenteBean(nome, email, password, "Cliente");
        RegistrazioneAppController appController = RegistrazioneAppController.getInstance();

        try {
            boolean successo = appController.registraNuovoCliente(nuovoUtente);
            if (successo) {
                mostraMessaggio("Registrazione avvenuta con successo!", false);
                // Si potrebbe fare un timer o cambiare scena subito
                // ma per ora chiediamo all'utente di cliccare sul link per tornare al login o ci andiamo in automatico
                tornaAlLoginAction();
            } else {
                mostraMessaggio("Errore: Impossibile registrare (email forse già in uso?)", true);
            }
        } catch (Exception e) {
            Printer.perror("Errore di registrazione: " + e.getMessage());
            mostraMessaggio("Errore di sistema. Riprova più tardi.", true);
        }
    }

    @FXML
    public void tornaAlLoginAction() {
        try {
            SceneManager.getInstance().cambiaScena(CostantiGUI.VIEW_LOGIN);
        } catch (Exception e) {
            Printer.perror("Errore tornando al login: " + e.getMessage());
        }
    }

    private void mostraMessaggio(String messaggio, boolean isError) {
        messageLabel.setText(messaggio);
        if (isError) {
            messageLabel.setTextFill(Color.web("#e74c3c"));
        } else {
            messageLabel.setTextFill(Color.web("#27ae60"));
        }
        messageLabel.setVisible(true);
    }
}
