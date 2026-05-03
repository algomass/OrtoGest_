package it.ortogest.ortogestapp.dao;

import it.ortogest.ortogestapp.model.CategoriaProdotto;
import it.ortogest.ortogestapp.model.Prodotto;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object Mock per i Prodotti (Catalogo).
 */
public class ProdottoDAO implements IProdottoDAO {
    
    // Mock del database: una lista statica
    private static final List<Prodotto> catalogoDB = new ArrayList<>();
    
    static {
        // --- FRUTTA ---
        catalogoDB.add(new Prodotto("Mele Golden",       1.50, 120.0, CategoriaProdotto.FRUTTA,  "/images/mele_golden.png"));
        catalogoDB.add(new Prodotto("Pere Abate",        2.20,  85.0, CategoriaProdotto.FRUTTA,  "/images/pere_abate.png"));
        catalogoDB.add(new Prodotto("Banane",            1.30, 200.0, CategoriaProdotto.FRUTTA,  "/images/banane.png"));
        catalogoDB.add(new Prodotto("Arance Tarocco",    1.80, 150.0, CategoriaProdotto.FRUTTA,  "/images/arance_tarocco.png"));
        catalogoDB.add(new Prodotto("Fragole",           3.90,  40.0, CategoriaProdotto.FRUTTA,  "/images/fragole.png"));

        // --- VERDURA ---
        catalogoDB.add(new Prodotto("Pomodori Datterino", 3.50, 90.0, CategoriaProdotto.VERDURA, "/images/pomodori_datterino.png"));
        catalogoDB.add(new Prodotto("Zucchine",           2.00, 70.0, CategoriaProdotto.VERDURA, "/images/zucchine.png"));
        catalogoDB.add(new Prodotto("Peperoni",            2.80, 55.0, CategoriaProdotto.VERDURA, "/images/peperoni.png"));
        catalogoDB.add(new Prodotto("Lattuga",             1.20, 60.0, CategoriaProdotto.VERDURA, "/images/lattuga.png"));
        catalogoDB.add(new Prodotto("Carote",              1.10, 110.0,CategoriaProdotto.VERDURA, "/images/carote.png"));
    }

    public List<Prodotto> getTuttiIProdotti() {
        return new ArrayList<>(catalogoDB);
    }

    public void salvaProdotto(Prodotto prodotto) {
        catalogoDB.add(prodotto);
    }

    public Prodotto trovaPerNome(String nome) {
        for (Prodotto p : catalogoDB) {
            if (p.getNome().equalsIgnoreCase(nome)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Restituisce i prodotti appartenenti a una determinata categoria.
     * @param categoria La categoria da filtrare ("Frutta" o "Verdura").
     * @return Lista dei prodotti della categoria specificata.
     */
    public List<Prodotto> trovaPerCategoria(String categoria) {
        List<Prodotto> risultati = new ArrayList<>();
        for (Prodotto p : catalogoDB) {
            if (p.getCategoria().equalsIgnoreCase(categoria)) {
                risultati.add(p);
            }
        }
        return risultati;
    }
}
