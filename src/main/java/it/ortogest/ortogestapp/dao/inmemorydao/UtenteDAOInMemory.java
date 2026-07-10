package it.ortogest.ortogestapp.dao.inmemorydao;

import it.ortogest.ortogestapp.dao.interfacedao.IUtenteDAO;
import it.ortogest.ortogestapp.model.Utente;

import java.util.ArrayList;
import java.util.List;

public class UtenteDAOInMemory implements IUtenteDAO {

    private List<Utente> utenti;

    public UtenteDAOInMemory() {
        this.utenti = new ArrayList<>();
        
        utenti.add(new Utente("Admin", "admin@ortogest.it", "admin123", "Responsabile"));
        utenti.add(new Utente("Mario", "mario@ortogest.it", "mario123", "Magazziniere"));
        utenti.add(new Utente("Luigi", "luigi@ortogest.it", "luigi123", "Operatore"));
        utenti.add(new Utente("Cliente Test", "cliente@test.it", "cliente123", "Cliente"));
        
        utenti.add(new Utente("A", "a", "a", "Responsabile"));
    }

    @Override
    public Utente verificaCredenziali(String email, String password) {
        return utenti.stream()
                .filter(u -> u.getEmail().equals(email) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean registraUtente(Utente utente) {
        // Controllo se l'email esiste già
        if (utenti.stream().anyMatch(u -> u.getEmail().equals(utente.getEmail()))) {
            return false;
        }
        utenti.add(utente);
        return true;
    }
}
