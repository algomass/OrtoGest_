package it.ortogest.ortogestapp.dao.InMemoryDAO;

import it.ortogest.ortogestapp.dao.InterfaceDAO.IProdottoDAO;
import it.ortogest.ortogestapp.model.Prodotto;

import java.util.ArrayList;
import java.util.List;

public class ProdottoDAOInMemory implements IProdottoDAO {

    private List<Prodotto> prodotti;

    public ProdottoDAOInMemory() {
        this.prodotti = new ArrayList<>();
        // Dati iniziali fittizi per la demo
        prodotti.add(new Prodotto("Mela Golden", 2.50, 100.0, "Frutta", "/images/mela_golden.png"));
        prodotti.add(new Prodotto("Zucchina Romana", 1.80, 50.0, "Verdura", "/images/zucchina.png"));
        prodotti.add(new Prodotto("Banana", 1.99, 120.0, "Frutta", "/images/banana.png"));
    }

    @Override
    public List<Prodotto> getTuttiIProdotti() {
        return new ArrayList<>(prodotti); // Copia difensiva
    }

    @Override
    public void salvaProdotto(Prodotto prodotto) {
        boolean found = false;
        for (int i = 0; i < prodotti.size(); i++) {
            if (prodotti.get(i).getNome().equalsIgnoreCase(prodotto.getNome())) {
                prodotti.set(i, prodotto);
                found = true;
                break;
            }
        }
        if (!found) {
            prodotti.add(prodotto);
        }
    }

    @Override
    public Prodotto trovaPerNome(String nome) {
        return prodotti.stream()
                .filter(p -> p.getNome().equalsIgnoreCase(nome))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Prodotto> trovaPerCategoria(String categoria) {
        return prodotti.stream()
                .filter(p -> p.getCategoria().equalsIgnoreCase(categoria))
                .toList();
    }

    @Override
    public void eliminaProdotto(String nome) {
        prodotti.removeIf(p -> p.getNome().equalsIgnoreCase(nome));
    }
}
