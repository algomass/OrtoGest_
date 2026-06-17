package it.ortogest.ortogestapp.dao.FileSystemDAO;

import it.ortogest.ortogestapp.dao.InterfaceDAO.IUtenteDAO;
import it.ortogest.ortogestapp.model.Utente;

import java.io.*;

public class UtenteDAOFileSystem implements IUtenteDAO {

    private static final String FILE_PATH = "data/utenti.csv";

    public UtenteDAOFileSystem() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        File f = new File(FILE_PATH);
        if (!f.exists()) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
                pw.println("nome,email,password,ruolo"); // Header
                // Inseriamo gli utenti di default come in SQL
                pw.println("Admin,admin@ortogest.it,admin123,Responsabile");
                pw.println("Mario,mario@ortogest.it,mario123,Magazziniere");
                pw.println("Luigi,luigi@ortogest.it,luigi123,Operatore");
                pw.println("Cliente Test,cliente@test.it,cliente123,Cliente");
            } catch (IOException e) {
                it.ortogest.ortogestapp.utils.Printer.perror("Errore I/O in UtenteDAOFileSystem: " + e.getMessage());
            }
        }
    }

    @Override
    public Utente verificaCredenziali(String email, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Skip header
                }
                String[] values = line.split(",");
                if (values.length >= 4 && values[1].equals(email) && values[2].equals(password)) {
                    return new Utente(values[0], values[1], values[2], values[3]);
                }
            }
        } catch (IOException e) {
            it.ortogest.ortogestapp.utils.Printer.perror("Errore I/O in UtenteDAOFileSystem: " + e.getMessage());
        }
        return null;
    }
}
