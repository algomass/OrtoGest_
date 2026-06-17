package it.ortogest.ortogestapp.dao.interfacedao;

import it.ortogest.ortogestapp.model.Lotto;
import java.util.List;

public interface ILottoDAO {
    void salvaLotto(Lotto lotto);

    List<Lotto> getTuttiILotti();

    Lotto trovaPerId(String idLotto);

    List<Lotto> trovaPerProdotto(String nomeProdotto);

    void eliminaLotto(String idLotto);

    double getPrezzoMedioAcquisto(String nomeProdotto);
}
