package it.ortogest.ortogestapp.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/ortogest?createDatabaseIfNotExist=true";
    private static final String DB_USER = "root";
    private static final String DB_PASS = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "root";
    private static final DatabaseHelper instance = new DatabaseHelper();

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

    
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    
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
                "smaltito BOOLEAN DEFAULT FALSE, " +
                "ritirato BOOLEAN DEFAULT FALSE, " +
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

            
            eseguiAlterTableSilenzioso(stmt, "ALTER TABLE lotto ADD COLUMN prezzo_vendita DOUBLE DEFAULT 0.0");
            eseguiAlterTableSilenzioso(stmt, "ALTER TABLE lotto ADD COLUMN sconto_attivo BOOLEAN DEFAULT FALSE");
            eseguiAlterTableSilenzioso(stmt, "ALTER TABLE lotto ADD COLUMN prezzo_scontato DOUBLE DEFAULT 0.0");
            eseguiAlterTableSilenzioso(stmt, "ALTER TABLE lotto ADD COLUMN smaltito BOOLEAN DEFAULT FALSE");
            eseguiAlterTableSilenzioso(stmt, "ALTER TABLE lotto ADD COLUMN ritirato BOOLEAN DEFAULT FALSE");
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
            // L'eccezione viene ignorata intenzionalmente perché, se la colonna esiste già,
            // l'ALTER TABLE fallirà, ma possiamo procedere tranquillamente.
        }
    }

    @FunctionalInterface
    public interface ResultSetExtractor<T> {
        T extract(ResultSet rs) throws SQLException;
    }

    public <T> T queryForObject(String sql, ResultSetExtractor<T> extractor, String errorMessage, Object... params) {
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractor.extract(rs);
                }
            }
        } catch (SQLException e) {
            Printer.perror(errorMessage + ": " + e.getMessage());
        }
        return null;
    }

    public <T> List<T> queryForList(String sql, ResultSetExtractor<T> extractor, String errorMessage,
            Object... params) {
        List<T> list = new ArrayList<>();
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(extractor.extract(rs));
                }
            }
        } catch (SQLException e) {
            Printer.perror(errorMessage + ": " + e.getMessage());
        }
        return list;
    }

    public void executeUpdate(String sql, String errorMessage, Object... params) {
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            Printer.perror(errorMessage + ": " + e.getMessage());
        }
    }
}
