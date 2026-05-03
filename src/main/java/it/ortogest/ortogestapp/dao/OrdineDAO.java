package it.ortogest.ortogestapp.dao;

import it.ortogest.ortogestapp.model.Ordine;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object Mock per gli Ordini.
 */
public class OrdineDAO {
    
    // Mock del database
    private static final List<Ordine> ordiniDB = new ArrayList<>();

    public void salvaOrdine(Ordine ordine) {
        ordiniDB.add(ordine);
    }

    public List<Ordine> trovaOrdiniCliente(String emailCliente) {
        List<Ordine> risultati = new ArrayList<>();
        for (Ordine o : ordiniDB) {
            if (o.getEmailCliente().equals(emailCliente)) {
                risultati.add(o);
            }
        }
        return risultati;
    }
}
