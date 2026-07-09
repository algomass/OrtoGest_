package it.ortogest.ortogestapp.pattern.abstractfactory;

import it.ortogest.ortogestapp.dao.filesystemdao.LottoDAOFileSystem;
import it.ortogest.ortogestapp.dao.filesystemdao.OrdineDAOFileSystem;
import it.ortogest.ortogestapp.dao.filesystemdao.ProdottoDAOFileSystem;
import it.ortogest.ortogestapp.dao.filesystemdao.UtenteDAOFileSystem;
import it.ortogest.ortogestapp.dao.interfacedao.ILottoDAO;
import it.ortogest.ortogestapp.dao.interfacedao.IOrdineDAO;
import it.ortogest.ortogestapp.dao.interfacedao.IProdottoDAO;
import it.ortogest.ortogestapp.dao.interfacedao.IUtenteDAO;


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
