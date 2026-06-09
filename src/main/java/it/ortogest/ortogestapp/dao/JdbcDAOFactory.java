package it.ortogest.ortogestapp.dao;

/**
 * Concrete Factory per la famiglia di oggetti DAO JDBC (Database).
 */
public class JdbcDAOFactory extends DAOFactory {

    @Override
    public IProdottoDAO getProdottoDAO() {
        return new ProdottoDAOJdbc();
    }

    @Override
    public ILottoDAO getLottoDAO() {
        return new LottoDAOJdbc();
    }

    @Override
    public IOrdineDAO getOrdineDAO() {
        return new OrdineDAOJdbc();
    }

    @Override
    public IUtenteDAO getUtenteDAO() {
        return new UtenteDAOJdbc();
    }
}
