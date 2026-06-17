package it.ortogest.ortogestapp.dao.InMemoryDAO;

import it.ortogest.ortogestapp.dao.InterfaceDAO.IOrdineDAO;
import it.ortogest.ortogestapp.model.Ordine;

import java.util.ArrayList;
import java.util.List;

public class OrdineDAOInMemory implements IOrdineDAO {

    private List<Ordine> ordini;

    public OrdineDAOInMemory() {
        this.ordini = new ArrayList<>();
        // Per gli ordini non carichiamo nulla di base per la demo, o se si vuole si può
        // aggiungere
    }

    @Override
    public void salvaOrdine(Ordine ordine) {
        boolean found = false;
        for (int i = 0; i < ordini.size(); i++) {
            if (ordini.get(i).getIdOrdine().equals(ordine.getIdOrdine())) {
                ordini.set(i, ordine);
                found = true;
                break;
            }
        }
        if (!found) {
            ordini.add(ordine);
        }
    }

    @Override
    public List<Ordine> trovaOrdiniCliente(String emailCliente) {
        return ordini.stream()
                .filter(o -> o.getEmailCliente().equalsIgnoreCase(emailCliente))
                .toList();
    }

    @Override
    public Ordine trovaOrdinePerId(String idOrdine) {
        return ordini.stream()
                .filter(o -> o.getIdOrdine().equals(idOrdine))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void eliminaOrdine(String idOrdine) {
        ordini.removeIf(o -> o.getIdOrdine().equals(idOrdine));
    }

    @Override
    public List<Ordine> trovaTuttiOrdini() {
        return new ArrayList<>(ordini);
    }

    @Override
    public void aggiornaStatoOrdine(String idOrdine, String nuovoStato) {
        for (Ordine o : ordini) {
            if (o.getIdOrdine().equals(idOrdine)) {
                o.setStato(nuovoStato);
                break;
            }
        }
    }
}
