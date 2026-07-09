package it.ortogest.ortogestapp.pattern.abstractfactory;

import it.ortogest.ortogestapp.dao.inmemorydao.LottoDAOInMemory;
import it.ortogest.ortogestapp.dao.inmemorydao.OrdineDAOInMemory;
import it.ortogest.ortogestapp.dao.inmemorydao.ProdottoDAOInMemory;
import it.ortogest.ortogestapp.dao.inmemorydao.UtenteDAOInMemory;
import it.ortogest.ortogestapp.dao.interfacedao.ILottoDAO;
import it.ortogest.ortogestapp.dao.interfacedao.IOrdineDAO;
import it.ortogest.ortogestapp.dao.interfacedao.IProdottoDAO;
import it.ortogest.ortogestapp.dao.interfacedao.IUtenteDAO;


public class InMemoryDAOFactory extends DAOFactory {

    private IProdottoDAO prodottoDAO;
    private ILottoDAO lottoDAO;
    private IOrdineDAO ordineDAO;
    private IUtenteDAO utenteDAO;

    public InMemoryDAOFactory() {
        
        this.prodottoDAO = new ProdottoDAOInMemory();
        this.lottoDAO = new LottoDAOInMemory(this.prodottoDAO);
        this.ordineDAO = new OrdineDAOInMemory();
        this.utenteDAO = new UtenteDAOInMemory();
    }

    @Override
    public IProdottoDAO getProdottoDAO() {
        return this.prodottoDAO;
    }

    @Override
    public ILottoDAO getLottoDAO() {
        return this.lottoDAO;
    }

    @Override
    public IOrdineDAO getOrdineDAO() {
        return this.ordineDAO;
    }

    @Override
    public IUtenteDAO getUtenteDAO() {
        return this.utenteDAO;
    }
}
