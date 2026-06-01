package it.ortogest.ortogestapp.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Classe di supporto per la gestione della connessione al database MySQL.
 * Implementa il pattern Singleton.
 */
public class DatabaseHelper {

    private static final DatabaseHelper instance = new DatabaseHelper();
    private static final String DB_URL = "jdbc:mysql://localhost:3306/ortogest?createDatabaseIfNotExist=true";
    private static final String DB_USER = "root";
    private static final String DB_PASS = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "root";

    private DatabaseHelper() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            inizializzaDatabase();
        } catch (ClassNotFoundException e) {
            Printer.perror("Driver MySQL non trovato: " + e.getMessage());
        }
    }

    public static DatabaseHelper getInstance() {
        return instance;
    }

    /**
     * Restituisce una nuova connessione al database.
     * È responsabilità di chi chiama questo metodo chiudere la connessione.
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    /**
     * Crea le tabelle se non esistono già.
     */
    private void inizializzaDatabase() {
        String sqlProdotto = "CREATE TABLE IF NOT EXISTS prodotto (" +
                "nome VARCHAR(255) PRIMARY KEY, " +
                "prezzo_attuale DOUBLE NOT NULL, " +
                "quantita_disponibile DOUBLE NOT NULL, " +
                "categoria VARCHAR(100) NOT NULL, " +
                "immagine_path VARCHAR(255)" +
                ");";

        String sqlLotto = "CREATE TABLE IF NOT EXISTS lotto (" +
                "id_lotto VARCHAR(100) PRIMARY KEY, " +
                "nome_fornitore VARCHAR(255) NOT NULL, " +
                "nome_prodotto VARCHAR(255) NOT NULL, " +
                "quantita_kg DOUBLE NOT NULL, " +
                "data_arrivo VARCHAR(50) NOT NULL, " +
                "data_scadenza VARCHAR(50) NOT NULL, " +
                "costo_acquisto DOUBLE NOT NULL, " +
                "prezzo_vendita DOUBLE DEFAULT 0.0, " +
                "sconto_attivo BOOLEAN DEFAULT FALSE, " +
                "prezzo_scontato DOUBLE DEFAULT 0.0, " +
                "FOREIGN KEY(nome_prodotto) REFERENCES prodotto(nome)" +
                ");";
                
        String sqlOrdine = "CREATE TABLE IF NOT EXISTS ordine (" +
                "id_ordine VARCHAR(100) PRIMARY KEY, " +
                "email_cliente VARCHAR(255) NOT NULL, " +
                "stato VARCHAR(50) NOT NULL" +
                ");";
                
        String sqlRigaOrdine = "CREATE TABLE IF NOT EXISTS riga_ordine (" +
                "id_riga INT AUTO_INCREMENT PRIMARY KEY, " +
                "id_ordine VARCHAR(100) NOT NULL, " +
                "id_lotto VARCHAR(100), " +
                "nome_prodotto VARCHAR(255) NOT NULL, " +
                "quantita DOUBLE NOT NULL, " +
                "prezzo_fissato DOUBLE NOT NULL, " +
                "FOREIGN KEY(id_ordine) REFERENCES ordine(id_ordine), " +
                "FOREIGN KEY(nome_prodotto) REFERENCES prodotto(nome)" +
                ");";

        String sqlUtente = "CREATE TABLE IF NOT EXISTS utente (" +
                "email VARCHAR(255) PRIMARY KEY, " +
                "password VARCHAR(255) NOT NULL, " +
                "ruolo VARCHAR(50) NOT NULL" +
                ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(sqlProdotto);
            stmt.execute(sqlLotto);
            stmt.execute(sqlOrdine);
            stmt.execute(sqlRigaOrdine);
            stmt.execute(sqlUtente);
            
            // Tentativo di ALTER TABLE per supportare aggiornamenti senza cancellare i dati
            eseguiAlterTableSilenzioso(stmt, "ALTER TABLE lotto ADD COLUMN prezzo_vendita DOUBLE DEFAULT 0.0");
            eseguiAlterTableSilenzioso(stmt, "ALTER TABLE lotto ADD COLUMN sconto_attivo BOOLEAN DEFAULT FALSE");
            eseguiAlterTableSilenzioso(stmt, "ALTER TABLE lotto ADD COLUMN prezzo_scontato DOUBLE DEFAULT 0.0");
            eseguiAlterTableSilenzioso(stmt, "ALTER TABLE riga_ordine ADD COLUMN id_lotto VARCHAR(100)");
            
            Printer.printf("Database MySQL inizializzato con successo.");
            
        } catch (SQLException e) {
            Printer.perror("Errore durante l'inizializzazione del database MySQL: " + e.getMessage());
        }
    }

    private void eseguiAlterTableSilenzioso(Statement stmt, String sql) {
        try {
            stmt.execute(sql);
        } catch (SQLException _) {
            // Ignora se la colonna esiste già
        }
    }
}
