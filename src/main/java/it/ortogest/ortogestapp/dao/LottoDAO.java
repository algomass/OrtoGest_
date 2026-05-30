package it.ortogest.ortogestapp.dao;

import it.ortogest.ortogestapp.model.Lotto;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object Mock per i Lotti.
 */
public class LottoDAO implements ILottoDAO {
    // Mock del database: una lista statica
    private static final List<Lotto> lottiDB = new ArrayList<>();

    public void salvaLotto(Lotto lotto) {
        lottiDB.add(lotto);
    }

    public List<Loatto> getTuttiILotti() {
        return new ArrayList<>(lottiDB);
    }
    
    public Lotto trovaPerId(String idLotto) {
        for (Lotto l : lottiDB) {
            if (l.getIdLotto().equalsIgnoreCase(idLotto)) {
                return l;
            }
        }
        return null;
    }

    public List<Lotto> trovaPerProdotto(String nomeProdotto) {
        List<Lotto> result = new ArrayList<>();
        for (Lotto l : lottiDB) {
            if (l.getTipologiaProdotto().getNome().equalsIgnoreCase(nomeProdotto)) {
                result.add(l);
            }
        }
        return result;
    }
}
