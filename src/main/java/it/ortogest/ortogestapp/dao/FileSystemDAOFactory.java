package it.ortogest.ortogestapp.dao;

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
