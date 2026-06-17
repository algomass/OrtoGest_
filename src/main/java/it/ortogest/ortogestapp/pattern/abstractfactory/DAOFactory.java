package it.ortogest.ortogestapp.pattern.abstractfactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import it.ortogest.ortogestapp.dao.interfacedao.ILottoDAO;
import it.ortogest.ortogestapp.dao.interfacedao.IOrdineDAO;
import it.ortogest.ortogestapp.dao.interfacedao.IProdottoDAO;
import it.ortogest.ortogestapp.dao.interfacedao.IUtenteDAO;

/**
 * Abstract Factory per disaccoppiare l'App Controller dalle implementazioni
 * concrete dei DAO.
 * Supporta l'istanziazione dinamica basata su configurazione (JDBC, CSV o
 * DEMO).
 * 
 * PATTERN APPLICATI:
 * 1. Abstract Factory: Questa classe astratta definisce le "fabbriche" per
 * creare oggetti DAO (Data Access Object).
 * 2. Singleton: L'intero sistema userÃƒÂ  una sola e unica istanza di questa
 * Factory.
 */
public abstract class DAOFactory {

    private static DAOFactory instance;

    public static final String JDBC = "jdbc";
    public static final String CSV = "csv";
    public static final String DEMO = "demo";

    protected DAOFactory() {
    }

    /**
     * Metodo per ottenere l'unica istanza globale del DAOFactory (Pattern
     * Singleton).
     * Questo metodo legge un file di configurazione per decidere a runtime
     * quale implementazione concreta di persistenza (Database, File o Memoria)
     * utilizzare.
     */
    public static DAOFactory getInstance() {
        // Passo 1: Pattern Singleton (Lazy Initialization). Se l'istanza non esiste
        // ancora, inizio a crearla.
        if (instance == null) {

            // Passo 2: Imposto il Database relazionale (JDBC) come modalitÃƒÂ  predefinita
            String type = JDBC;

            try {
                // Passo 3: Provo a caricare il file "config.properties" dalla root del progetto
                File configFile = new File("config.properties");
                if (configFile.exists()) {
                    Properties props = new Properties();

                    // Passo 4: Apro il file in lettura. Il try-with-resources assicura la chiusura
                    // del file
                    try (FileInputStream fis = new FileInputStream(configFile)) {
                        props.load(fis);

                        // Passo 5: Leggo la proprietÃƒÂ  "persistence". Se manca, uso "JDBC"
                        type = props.getProperty("persistence", JDBC).trim().toLowerCase();
                    }
                }
            } catch (Exception _) {
                // Passo 6: Fallback sicuro. Se il file ÃƒÂ¨ corrotto, avviso e proseguo con il
                // database di default
                it.ortogest.ortogestapp.utils.Printer
                        .perror("Errore nella lettura di config.properties. Uso JDBC di default.");
            }

            // Passo 7: Polymorphism (Polimorfismo). In base al tipo configurato, istanzio
            // la Factory concreta
            if (DEMO.equals(type)) {
                // ModalitÃƒÂ  "demo": Nessun salvataggio permanente, usa solo la memoria RAM
                // (ottimo per i test)
                instance = new InMemoryDAOFactory();
            } else if (CSV.equals(type)) {
                // ModalitÃƒÂ  "csv": Salva e legge i dati su file di testo (.csv)
                instance = new FileSystemDAOFactory();
            } else {
                // ModalitÃƒÂ  "jdbc": Connessione reale al database MySQL
                instance = new JdbcDAOFactory();
            }
        }

        // Passo 8: Restituisco l'istanza. Le successive chiamate salteranno il Passo 1
        // e restituiranno direttamente questa.
        return instance;
    }

    public abstract IProdottoDAO getProdottoDAO();

    public abstract ILottoDAO getLottoDAO();

    public abstract IOrdineDAO getOrdineDAO();

    public abstract IUtenteDAO getUtenteDAO();
}
