package it.ortogest.ortogestapp.dao;

/**
 * Concrete Factory per la famiglia di oggetti DAO In-Memory (Versione Demo).
 * Mantiene le singole istanze dei DAO in modo che i dati in RAM persistano
 * per l'intera durata della sessione applicativa, senza andare persi tra
 * una chiamata e l'altra dei controller.
 */
public class InMemoryDAOFactory extends DAOFactory {

    private IProdottoDAO prodottoDAO;
    private ILottoDAO lottoDAO;
    private IOrdineDAO ordineDAO;
    private IUtenteDAO utenteDAO;

    public InMemoryDAOFactory() {
        // Inizializza i DAO una sola volta per mantenere i dati in memoria
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
