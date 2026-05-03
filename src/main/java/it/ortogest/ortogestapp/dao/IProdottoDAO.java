package it.ortogest.ortogestapp.dao;

import it.ortogest.ortogestapp.model.Prodotto;
import java.util.List;

public interface IProdottoDAO {
    List<Prodotto> getTuttiIProdotti();
    void salvaProdotto(Prodotto prodotto);
    Prodotto trovaPerNome(String nome);
    List<Prodotto> trovaPerCategoria(String categoria);
}
