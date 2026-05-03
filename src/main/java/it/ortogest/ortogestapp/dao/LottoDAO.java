package it.ortogest.ortogestapp.dao;

import it.ortogest.ortogestapp.model.Lotto;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object Mock per i Lotti.
 */
public class LottoDAO {
    // Mock del database: una lista statica
    private static final List<Lotto> lottiDB = new ArrayList<>();

    public void salvaLotto(Lotto lotto) {
        lottiDB.add(lotto);
    }

    public List<Lotto> getTuttiILotti() {
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
}
