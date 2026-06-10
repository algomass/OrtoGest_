package it.ortogest.ortogestapp.dao;

import it.ortogest.ortogestapp.model.Lotto;
import it.ortogest.ortogestapp.model.Prodotto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LottoDAOInMemory implements ILottoDAO {

    private List<Lotto> lotti;

    public LottoDAOInMemory(IProdottoDAO prodottoDAO) {
        this.lotti = new ArrayList<>();
        // Per popolare i lotti demo usiamo il prodottoDAO passato
        Prodotto mela = prodottoDAO.trovaPerNome("Mela Golden");
        Prodotto zucchina = prodottoDAO.trovaPerNome("Zucchina Romana");

        if (mela != null) {
            lotti.add(Lotto.builder()
                    .idLotto("LOT-DEMO-1")
                    .nomeFornitore("Fattoria Rossi")
                    .tipologiaProdotto(mela)
                    .quantitaKg(100.0)
                    .dataArrivo(LocalDate.now(java.time.ZoneId.systemDefault()).minusDays(2))
                    .dataScadenza(LocalDate.now(java.time.ZoneId.systemDefault()).plusDays(10))
                    .costoAcquisto(1.20)
                    .prezzoVendita(2.50)
                    .scontoScadenzaAttivo(false)
                    .prezzoScontato(2.50)
                    .build());
        }

        if (zucchina != null) {
            lotti.add(Lotto.builder()
                    .idLotto("LOT-DEMO-2")
                    .nomeFornitore("Orto Verdi")
                    .tipologiaProdotto(zucchina)
                    .quantitaKg(50.0)
                    .dataArrivo(LocalDate.now(java.time.ZoneId.systemDefault()).minusDays(1))
                    .dataScadenza(LocalDate.now(java.time.ZoneId.systemDefault()).plusDays(5))
                    .costoAcquisto(0.80)
                    .prezzoVendita(1.80)
                    .scontoScadenzaAttivo(false)
                    .prezzoScontato(1.80)
                    .build());
        }
    }

    @Override
    public void salvaLotto(Lotto lotto) {
        boolean found = false;
        for (int i = 0; i < lotti.size(); i++) {
            if (lotti.get(i).getIdLotto().equals(lotto.getIdLotto())) {
                lotti.set(i, lotto);
                found = true;
                break;
            }
        }
        if (!found) {
            lotti.add(lotto);
        }
    }

    @Override
    public List<Lotto> getTuttiILotti() {
        return new ArrayList<>(lotti);
    }

    @Override
    public Lotto trovaPerId(String idLotto) {
        return lotti.stream()
                .filter(l -> l.getIdLotto().equals(idLotto))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Lotto> trovaPerProdotto(String nomeProdotto) {
        return lotti.stream()
                .filter(l -> l.getTipologiaProdotto() != null && l.getTipologiaProdotto().getNome().equalsIgnoreCase(nomeProdotto))
                .toList();
    }

    @Override
    public void eliminaLotto(String idLotto) {
        lotti.removeIf(l -> l.getIdLotto().equals(idLotto));
    }

    @Override
    public double getPrezzoMedioAcquisto(String nomeProdotto) {
        List<Lotto> lottiFiltrati = trovaPerProdotto(nomeProdotto);
        if (lottiFiltrati.isEmpty()) return 0.0;
        double sum = 0;
        for (Lotto l : lottiFiltrati) {
            sum += l.getCostoAcquisto();
        }
        return sum / lottiFiltrati.size();
    }
}
