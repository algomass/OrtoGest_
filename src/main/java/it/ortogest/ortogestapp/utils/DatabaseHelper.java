package it.ortogest.ortogestapp.utils;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DatabaseHelper {

    private String dbUrl = "jdbc:mysql://localhost:3306/ortogest?createDatabaseIfNotExist=true";
    private String dbUser = "root";
    private String dbPass = "";
    private static final DatabaseHelper instance = new DatabaseHelper();

    private DatabaseHelper() {
        try {
            File configFile = new File("config.properties");
            if (configFile.exists()) {
                Properties props = new Properties();
                try (FileInputStream fis = new FileInputStream(configFile)) {
                    props.load(fis);
                    dbUrl = props.getProperty("db.url", dbUrl);
                    dbUser = props.getProperty("db.user", dbUser);
                    dbPass = props.getProperty("db.password", dbPass);
                }
            }

            if (System.getenv("DB_PASSWORD") != null) {
                dbPass = System.getenv("DB_PASSWORD");
            }

            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            Printer.perror("Errore nell'inizializzazione di DatabaseHelper: " + e.getMessage());
        }
    }

    public static DatabaseHelper getInstance() {
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUser, dbPass);
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
