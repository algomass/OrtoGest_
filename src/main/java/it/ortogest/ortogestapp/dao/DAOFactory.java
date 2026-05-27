package it.ortogest.ortogestapp.dao;

/**
 * Abstract Factory per disaccoppiare l'App Controller dalle implementazioni concrete dei DAO.
 */
@SuppressWarnings("java:S6548")
public class DAOFactory {
    
    private static DAOFactory instance;
    
    private DAOFactory() {}
    
    public static DAOFactory getInstance() {
        if (instance == null) {
            instance = new DAOFactory();
        }
        return instance;
    }
    
    public IProdottoDAO getProdottoDAO() {
        return new ProdottoDAOJdbc();
    }
    
    public ILottoDAO getLottoDAO() {
        return new LottoDAOJdbc();
    }
    
    public IOrdineDAO getOrdineDAO() {
        return new OrdineDAOJdbc();
    }
    
    public IUtenteDAO getUtenteDAO() {
        return new UtenteDAOJdbc();
    }
}
