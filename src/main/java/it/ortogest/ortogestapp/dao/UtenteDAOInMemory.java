package it.ortogest.ortogestapp.dao;

import it.ortogest.ortogestapp.model.Utente;

import java.util.ArrayList;
import java.util.List;

public class UtenteDAOInMemory implements IUtenteDAO {

    private List<Utente> utenti;

    public UtenteDAOInMemory() {
        this.utenti = new ArrayList<>();
        // Popolamento iniziale fittizio per la Versione Demo
        utenti.add(new Utente("Admin", "admin@ortogest.it", "admin123", "Responsabile"));
        utenti.add(new Utente("Mario", "mario@ortogest.it", "mario123", "Magazziniere"));
        utenti.add(new Utente("Luigi", "luigi@ortogest.it", "luigi123", "Operatore"));
        utenti.add(new Utente("Cliente Test", "cliente@test.it", "cliente123", "Cliente"));
        // Aggiunti anche gli utenti per i test rapidi come richiesto
        utenti.add(new Utente("A", "a", "a", "Responsabile"));
    }

    @Override
    public Utente verificaCredenziali(String email, String password) {
        return utenti.stream()
                .filter(u -> u.getEmail().equals(email) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }
}
