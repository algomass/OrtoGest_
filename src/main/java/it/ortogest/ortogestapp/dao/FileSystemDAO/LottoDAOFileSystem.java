package it.ortogest.ortogestapp.dao.FileSystemDAO;

import it.ortogest.ortogestapp.dao.InterfaceDAO.ILottoDAO;
import it.ortogest.ortogestapp.model.Lotto;
import it.ortogest.ortogestapp.model.Prodotto;
import it.ortogest.ortogestapp.pattern.AbstractFactory.DAOFactory;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LottoDAOFileSystem implements ILottoDAO {

    private static final String FILE_PATH = "data/lotti.csv";
    private static final String ERRORE_IO_MSG = "Errore I/O in LottoDAOFileSystem: ";

    public LottoDAOFileSystem() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        File f = new File(FILE_PATH);
        if (!f.exists()) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
                pw.println(
                        "idLotto,nomeFornitore,nomeProdotto,quantitaKg,dataArrivo,dataScadenza,costoAcquisto,prezzoVendita,scontoAttivo,prezzoScontato");
            } catch (IOException e) {
                it.ortogest.ortogestapp.utils.Printer.perror(ERRORE_IO_MSG + e.getMessage());
            }
        }
    }

    private List<Lotto> leggiTutti() {
        List<Lotto> lotti = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] values = line.split(",");
                if (values.length >= 10) {
                    String idLotto = values[0];
                    String nomeFornitore = values[1];
                    String nomeProdotto = values[2];
                    double quantitaKg = Double.parseDouble(values[3]);
                    LocalDate dataArrivo = LocalDate.parse(values[4]);
                    LocalDate dataScadenza = LocalDate.parse(values[5]);
                    double costoAcquisto = Double.parseDouble(values[6]);
                    double prezzoVendita = Double.parseDouble(values[7]);
                    boolean scontoAttivo = Boolean.parseBoolean(values[8]);
                    double prezzoScontato = Double.parseDouble(values[9]);

                    Prodotto prodotto = DAOFactory.getInstance().getProdottoDAO().trovaPerNome(nomeProdotto);

                    Lotto lotto = Lotto.builder()
                            .idLotto(idLotto)
                            .nomeFornitore(nomeFornitore)
                            .tipologiaProdotto(prodotto)
                            .quantitaKg(quantitaKg)
                            .dataArrivo(dataArrivo)
                            .dataScadenza(dataScadenza)
                            .costoAcquisto(costoAcquisto)
                            .prezzoVendita(prezzoVendita)
                            .scontoScadenzaAttivo(scontoAttivo)
                            .prezzoScontato(prezzoScontato)
                            .build();

                    lotti.add(lotto);
                }
            }
        } catch (IOException e) {
            it.ortogest.ortogestapp.utils.Printer.perror(ERRORE_IO_MSG + e.getMessage());
        }
        return lotti;
    }

    private void scriviTutti(List<Lotto> lotti) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            pw.println(
                    "idLotto,nomeFornitore,nomeProdotto,quantitaKg,dataArrivo,dataScadenza,costoAcquisto,prezzoVendita,scontoAttivo,prezzoScontato");
            for (Lotto l : lotti) {
                String pName = l.getTipologiaProdotto() != null ? l.getTipologiaProdotto().getNome() : "";
                pw.println(l.getIdLotto() + "," +
                        l.getNomeFornitore() + "," +
                        pName + "," +
                        l.getQuantitaKg() + "," +
                        l.getDataArrivo() + "," +
                        l.getDataScadenza() + "," +
                        l.getCostoAcquisto() + "," +
                        l.getPrezzoVendita() + "," +
                        l.isScontoScadenzaAttivo() + "," +
                        l.getPrezzoScontato());
            }
        } catch (IOException e) {
            it.ortogest.ortogestapp.utils.Printer.perror(ERRORE_IO_MSG + e.getMessage());
        }
    }

    @Override
    public void salvaLotto(Lotto lotto) {
        List<Lotto> lotti = leggiTutti();
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
        scriviTutti(lotti);
    }

    @Override
    public List<Lotto> getTuttiILotti() {
        return leggiTutti();
    }

    @Override
    public Lotto trovaPerId(String idLotto) {
        return leggiTutti().stream()
                .filter(l -> l.getIdLotto().equals(idLotto))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Lotto> trovaPerProdotto(String nomeProdotto) {
        return leggiTutti().stream()
                .filter(l -> l.getTipologiaProdotto() != null
                        && l.getTipologiaProdotto().getNome().equalsIgnoreCase(nomeProdotto))
                .toList();
    }

    @Override
    public void eliminaLotto(String idLotto) {
        List<Lotto> lotti = leggiTutti();
        lotti.removeIf(l -> l.getIdLotto().equals(idLotto));
        scriviTutti(lotti);
    }

    @Override
    public double getPrezzoMedioAcquisto(String nomeProdotto) {
        List<Lotto> lotti = trovaPerProdotto(nomeProdotto);
        if (lotti.isEmpty())
            return 0.0;
        double sum = 0;
        for (Lotto l : lotti) {
            sum += l.getCostoAcquisto();
        }
        return sum / lotti.size();
    }
}
