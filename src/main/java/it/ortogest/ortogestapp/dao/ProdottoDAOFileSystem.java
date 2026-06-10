package it.ortogest.ortogestapp.dao;

import it.ortogest.ortogestapp.model.Prodotto;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProdottoDAOFileSystem implements IProdottoDAO {

    private static final String FILE_PATH = "data/prodotti.csv";

    public ProdottoDAOFileSystem() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        File f = new File(FILE_PATH);
        if (!f.exists()) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
                pw.println("nome,prezzoAttuale,quantitaTotaleDisponibile,categoria,immaginePath");
                // Insert some default products
                pw.println("Mela Golden,2.50,100.0,Frutta,/images/mela_golden.png");
                pw.println("Zucchina Romana,1.80,50.0,Verdura,/images/zucchina.png");
            } catch (IOException e) {
                it.ortogest.ortogestapp.utils.Printer.perror("Errore I/O in ProdottoDAOFileSystem: " + e.getMessage());
            }
        }
    }

    private List<Prodotto> leggiTutti() {
        List<Prodotto> prodotti = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] values = line.split(",");
                if (values.length >= 5) {
                    Prodotto p = new Prodotto(
                            values[0],
                            Double.parseDouble(values[1]),
                            Double.parseDouble(values[2]),
                            values[3],
                            values[4]
                    );
                    prodotti.add(p);
                }
            }
        } catch (IOException e) {
            it.ortogest.ortogestapp.utils.Printer.perror("Errore I/O in ProdottoDAOFileSystem: " + e.getMessage());
        }
        return prodotti;
    }

    private void scriviTutti(List<Prodotto> prodotti) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            pw.println("nome,prezzoAttuale,quantitaTotaleDisponibile,categoria,immaginePath");
            for (Prodotto p : prodotti) {
                pw.println(p.getNome() + "," + p.getPrezzoAttuale() + "," +
                           p.getQuantitaTotaleDisponibile() + "," + p.getCategoria() + "," + p.getImmaginePath());
            }
        } catch (IOException e) {
            it.ortogest.ortogestapp.utils.Printer.perror("Errore I/O in ProdottoDAOFileSystem: " + e.getMessage());
        }
    }

    @Override
    public List<Prodotto> getTuttiIProdotti() {
        return leggiTutti();
    }

    @Override
    public void salvaProdotto(Prodotto prodotto) {
        List<Prodotto> prodotti = leggiTutti();
        boolean found = false;
        for (int i = 0; i < prodotti.size(); i++) {
            if (prodotti.get(i).getNome().equalsIgnoreCase(prodotto.getNome())) {
                prodotti.set(i, prodotto); // Update
                found = true;
                break;
            }
        }
        if (!found) {
            prodotti.add(prodotto); // Insert
        }
        scriviTutti(prodotti);
    }

    @Override
    public Prodotto trovaPerNome(String nome) {
        return leggiTutti().stream()
                .filter(p -> p.getNome().equalsIgnoreCase(nome))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Prodotto> trovaPerCategoria(String categoria) {
        return leggiTutti().stream()
                .filter(p -> p.getCategoria().equalsIgnoreCase(categoria))
                .toList();
    }

    @Override
    public void eliminaProdotto(String nome) {
        List<Prodotto> prodotti = leggiTutti();
        prodotti.removeIf(p -> p.getNome().equalsIgnoreCase(nome));
        scriviTutti(prodotti);
    }
}
