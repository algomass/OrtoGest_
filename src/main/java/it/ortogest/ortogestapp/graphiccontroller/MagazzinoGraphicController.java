package it.ortogest.ortogestapp.graphiccontroller;

import it.ortogest.ortogestapp.beans.ProdottoBean;
import it.ortogest.ortogestapp.dao.ProdottoDAO;
import it.ortogest.ortogestapp.model.Prodotto;
import it.ortogest.ortogestapp.utils.Printer;
import it.ortogest.ortogestapp.utils.SceneManager;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller Grafico (Boundary) per la schermata del Magazzino.
 */
public class MagazzinoGraphicController extends BaseGraphicController {

    @FXML private TableView<ProdottoBean> inventarioTable;
    @FXML private TableColumn<ProdottoBean, String> colNome;
    @FXML private TableColumn<ProdottoBean, Number> colPrezzo;
    @FXML private TableColumn<ProdottoBean, Number> colGiacenza;

    @FXML
    public void initialize() {
        // Configurazione delle colonne con le proprietà del ProdottoBean
        colNome.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNome()));
        colPrezzo.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrezzoAttuale()));
        colGiacenza.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getGiacenza()));

        caricaInventario();
    }

    private void caricaInventario() {
        ProdottoDAO prodottoDAO = new ProdottoDAO();
        List<Prodotto> prodotti = prodottoDAO.getTuttiIProdotti();
        List<ProdottoBean> beans = new ArrayList<>();
        for (Prodotto p : prodotti) {
            beans.add(new ProdottoBean(p.getNome(), p.getPrezzoAttuale(), p.getQuantitaTotaleDisponibile(), p.getCategoria(), p.getImmaginePath()));
        }
        ObservableList<ProdottoBean> data = FXCollections.observableArrayList(beans);
        inventarioTable.setItems(data);
    }

    @FXML
    public void apriSegnalazioneAnomaliaAction() {
        try {
            SceneManager.getInstance().cambiaScena("/GUI/SegnalazioneAnomalia.fxml");
        } catch (IOException e) {
            Printer.perror("Errore nell'apertura della schermata di segnalazione: " + e.getMessage());
        }
    }

    @FXML
    public void registraLottoAction() {
        try {
            SceneManager.getInstance().cambiaScena("/GUI/RegistrazioneLotto.fxml");
        } catch (IOException e) {
            Printer.perror("Errore nell'apertura della schermata di registrazione lotto: " + e.getMessage());
        }
    }
}
