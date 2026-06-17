package it.ortogest.ortogestapp.dao.FileSystemDAO;

import it.ortogest.ortogestapp.dao.InterfaceDAO.IOrdineDAO;
import it.ortogest.ortogestapp.model.Ordine;
import it.ortogest.ortogestapp.model.RigaOrdine;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class OrdineDAOFileSystem implements IOrdineDAO {

    private static final String FILE_PATH = "data/ordini.csv";
    private static final String ERRORE_IO_MSG = "Errore I/O in OrdineDAOFileSystem: ";

    public OrdineDAOFileSystem() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        File f = new File(FILE_PATH);
        if (!f.exists()) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
                pw.println("idOrdine,emailCliente,stato,righe");
            } catch (IOException e) {
                it.ortogest.ortogestapp.utils.Printer.perror(ERRORE_IO_MSG + e.getMessage());
            }
        }
    }

    private List<Ordine> leggiTutti() {
        List<Ordine> ordini = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] values = line.split(",", -1);
                if (values.length >= 4) {
                    List<RigaOrdine> righe = parseRigheOrdine(values[3]);
                    ordini.add(new Ordine(values[0], values[1], righe, values[2]));
                }
            }
        } catch (IOException e) {
            it.ortogest.ortogestapp.utils.Printer.perror(ERRORE_IO_MSG + e.getMessage());
        }
        return ordini;
    }

    private List<RigaOrdine> parseRigheOrdine(String righeStr) {
        List<RigaOrdine> righe = new ArrayList<>();
        if (righeStr.isEmpty()) {
            return righe;
        }
        String[] righeArray = righeStr.split("\\|");
        for (String rStr : righeArray) {
            String[] rVals = rStr.split(":");
            if (rVals.length == 4) {
                righe.add(
                        new RigaOrdine(rVals[0], rVals[1], Double.parseDouble(rVals[2]), Double.parseDouble(rVals[3])));
            }
        }
        return righe;
    }

    private void scriviTutti(List<Ordine> ordini) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            pw.println("idOrdine,emailCliente,stato,righe");
            for (Ordine o : ordini) {
                StringBuilder righeSb = new StringBuilder();
                for (int i = 0; i < o.getRighe().size(); i++) {
                    RigaOrdine r = o.getRighe().get(i);
                    // formato: nome:idLotto:qta:prezzo
                    righeSb.append(r.getNomeProdotto()).append(":")
                            .append(r.getIdLotto()).append(":")
                            .append(r.getQuantita()).append(":")
                            .append(r.getPrezzoUnitario());
                    if (i < o.getRighe().size() - 1) {
                        righeSb.append("|");
                    }
                }
                pw.println(o.getIdOrdine() + "," +
                        o.getEmailCliente() + "," +
                        o.getStato() + "," +
                        righeSb.toString());
            }
        } catch (IOException e) {
            it.ortogest.ortogestapp.utils.Printer.perror(ERRORE_IO_MSG + e.getMessage());
        }
    }

    @Override
    public void salvaOrdine(Ordine ordine) {
        List<Ordine> ordini = leggiTutti();
        boolean found = false;
        for (int i = 0; i < ordini.size(); i++) {
            if (ordini.get(i).getIdOrdine().equals(ordine.getIdOrdine())) {
                ordini.set(i, ordine);
                found = true;
                break;
            }
        }
        if (!found) {
            ordini.add(ordine);
        }
        scriviTutti(ordini);
    }

    @Override
    public List<Ordine> trovaOrdiniCliente(String emailCliente) {
        return leggiTutti().stream()
                .filter(o -> o.getEmailCliente().equalsIgnoreCase(emailCliente))
                .toList();
    }

    @Override
    public Ordine trovaOrdinePerId(String idOrdine) {
        return leggiTutti().stream()
                .filter(o -> o.getIdOrdine().equals(idOrdine))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void eliminaOrdine(String idOrdine) {
        List<Ordine> ordini = leggiTutti();
        ordini.removeIf(o -> o.getIdOrdine().equals(idOrdine));
        scriviTutti(ordini);
    }

    @Override
    public List<Ordine> trovaTuttiOrdini() {
        return leggiTutti();
    }

    @Override
    public void aggiornaStatoOrdine(String idOrdine, String nuovoStato) {
        List<Ordine> ordini = leggiTutti();
        for (Ordine o : ordini) {
            if (o.getIdOrdine().equals(idOrdine)) {
                o.setStato(nuovoStato);
                break;
            }
        }
        scriviTutti(ordini);
    }
}
