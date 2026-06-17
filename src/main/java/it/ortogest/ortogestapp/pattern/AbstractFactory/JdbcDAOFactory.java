package it.ortogest.ortogestapp.pattern.AbstractFactory;

import it.ortogest.ortogestapp.dao.InterfaceDAO.ILottoDAO;
import it.ortogest.ortogestapp.dao.InterfaceDAO.IOrdineDAO;
import it.ortogest.ortogestapp.dao.InterfaceDAO.IProdottoDAO;
import it.ortogest.ortogestapp.dao.InterfaceDAO.IUtenteDAO;
import it.ortogest.ortogestapp.dao.jdbcDAO.LottoDAOJdbc;
import it.ortogest.ortogestapp.dao.jdbcDAO.OrdineDAOJdbc;
import it.ortogest.ortogestapp.dao.jdbcDAO.ProdottoDAOJdbc;
import it.ortogest.ortogestapp.dao.jdbcDAO.UtenteDAOJdbc;

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
