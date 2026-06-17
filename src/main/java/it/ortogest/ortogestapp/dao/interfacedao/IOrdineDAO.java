package it.ortogest.ortogestapp.dao.interfacedao;

import it.ortogest.ortogestapp.model.Ordine;
import java.util.List;

public interface IOrdineDAO {
    void salvaOrdine(Ordine ordine);

    List<Ordine> trovaOrdiniCliente(String emailCliente);

    Ordine trovaOrdinePerId(String idOrdine);

    void eliminaOrdine(String idOrdine);

    List<Ordine> trovaTuttiOrdini();

    void aggiornaStatoOrdine(String idOrdine, String nuovoStato);
}
