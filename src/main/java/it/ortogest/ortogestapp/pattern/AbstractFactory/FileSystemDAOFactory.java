package it.ortogest.ortogestapp.pattern.AbstractFactory;

import it.ortogest.ortogestapp.dao.FileSystemDAO.LottoDAOFileSystem;
import it.ortogest.ortogestapp.dao.FileSystemDAO.OrdineDAOFileSystem;
import it.ortogest.ortogestapp.dao.FileSystemDAO.ProdottoDAOFileSystem;
import it.ortogest.ortogestapp.dao.FileSystemDAO.UtenteDAOFileSystem;
import it.ortogest.ortogestapp.dao.InterfaceDAO.ILottoDAO;
import it.ortogest.ortogestapp.dao.InterfaceDAO.IOrdineDAO;
import it.ortogest.ortogestapp.dao.InterfaceDAO.IProdottoDAO;
import it.ortogest.ortogestapp.dao.InterfaceDAO.IUtenteDAO;

/**
 * Concrete Factory per la famiglia di oggetti DAO basati su File System (CSV).
 */
public class FileSystemDAOFactory extends DAOFactory {

    @Override
    public IProdottoDAO getProdottoDAO() {
        return new ProdottoDAOFileSystem();
    }

    @Override
    public ILottoDAO getLottoDAO() {
        return new LottoDAOFileSystem();
    }

    @Override
    public IOrdineDAO getOrdineDAO() {
        return new OrdineDAOFileSystem();
    }

    @Override
    public IUtenteDAO getUtenteDAO() {
        return new UtenteDAOFileSystem();
    }
}
