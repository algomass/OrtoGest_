package it.ortogest.ortogestapp.pattern.abstractfactory;

import it.ortogest.ortogestapp.dao.interfacedao.ILottoDAO;
import it.ortogest.ortogestapp.dao.interfacedao.IOrdineDAO;
import it.ortogest.ortogestapp.dao.interfacedao.IProdottoDAO;
import it.ortogest.ortogestapp.dao.interfacedao.IUtenteDAO;
import it.ortogest.ortogestapp.dao.jdbcdao.LottoDAOJdbc;
import it.ortogest.ortogestapp.dao.jdbcdao.OrdineDAOJdbc;
import it.ortogest.ortogestapp.dao.jdbcdao.ProdottoDAOJdbc;
import it.ortogest.ortogestapp.dao.jdbcdao.UtenteDAOJdbc;

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
