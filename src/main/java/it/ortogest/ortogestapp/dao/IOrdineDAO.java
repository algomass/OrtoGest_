package it.ortogest.ortogestapp.dao;

import it.ortogest.ortogestapp.model.Ordine;
import java.util.List;

public interface IOrdineDAO {
    void salvaOrdine(Ordine ordine);
    List<Ordine> trovaOrdiniCliente(String emailCliente);
}
