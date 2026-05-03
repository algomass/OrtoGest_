package it.ortogest.ortogestapp.utils;

import it.ortogest.ortogestapp.beans.UtenteBean;

/**
 * SessionManager è un Singleton che gestisce i dati di sessione (es. l'utente attualmente loggato).
 * La sua singola responsabilità è mantenere lo stato globale della sessione utente.
 * In questo modo si disaccoppiano i controller, evitando di dover passare le informazioni 
 * dell'utente di scena in scena tramite costruttori o metodi ad hoc.
 */
public class SessionManager {
    private static SessionManager instance;
    private UtenteBean currentUser;
    private java.util.List<it.ortogest.ortogestapp.beans.RigaOrdineBean> currentCart;

    // Costruttore privato per impedire istanziazioni multiple dall'esterno
    private SessionManager() {}

    // Metodo per ottenere l'unica istanza (Singleton)
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // Metodo per effettuare il "login" nella sessione salvando l'utente
    public void login(UtenteBean utente) {
        this.currentUser = utente;
    }

    // Metodo per recuperare l'utente attualmente loggato
    public UtenteBean getCurrentUser() {
        return currentUser;
    }
    
    // Gestione carrello per passaggi tra scene
    public void setCarrelloCorrente(java.util.List<it.ortogest.ortogestapp.beans.RigaOrdineBean> carrello) {
        this.currentCart = carrello;
    }

    public java.util.List<it.ortogest.ortogestapp.beans.RigaOrdineBean> getCarrelloCorrente() {
        return currentCart;
    }

    // Metodo per la disconnessione
    public void logout() {
        this.currentUser = null;
        this.currentCart = null;
    }
}
