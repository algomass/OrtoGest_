package it.ortogest.ortogestapp.dao;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Abstract Factory per disaccoppiare l'App Controller dalle implementazioni concrete dei DAO.
 * Ora supporta l'istanziazione dinamica basata su configurazione (JDBC o CSV).
 */
public abstract class DAOFactory {
    
    private static DAOFactory instance;
    
    public static final String JDBC = "jdbc";
    public static final String CSV = "csv";
    public static final String DEMO = "demo";
    
    protected DAOFactory() {}
    
    public static DAOFactory getInstance() {
        if (instance == null) {
            String type = JDBC; // Valore di default
            try {
                File configFile = new File("config.properties");
                if (configFile.exists()) {
                    Properties props = new Properties();
                    try (FileInputStream fis = new FileInputStream(configFile)) {
                        props.load(fis);
                        type = props.getProperty("persistence", JDBC).trim().toLowerCase();
                    }
                }
            } catch (Exception e) {
                System.err.println("Errore nella lettura di config.properties. Uso JDBC di default.");
            }
            
            if (DEMO.equals(type)) {
                instance = new InMemoryDAOFactory();
            } else if (CSV.equals(type)) {
                instance = new FileSystemDAOFactory();
            } else {
                instance = new JdbcDAOFactory();
            }
        }
        return instance;
    }
    
    public abstract IProdottoDAO getProdottoDAO();
    public abstract ILottoDAO getLottoDAO();
    public abstract IOrdineDAO getOrdineDAO();
    public abstract IUtenteDAO getUtenteDAO();
}
