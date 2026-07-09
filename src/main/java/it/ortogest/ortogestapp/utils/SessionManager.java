package it.ortogest.ortogestapp.utils;

import it.ortogest.ortogestapp.beans.UtenteBean;


@SuppressWarnings("java:S6548")
public class SessionManager {
    private static SessionManager instance;
    private UtenteBean currentUser;
    private java.util.List<it.ortogest.ortogestapp.beans.RigaOrdineBean> currentCart;

    
    private SessionManager() {}

    
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    
    public void login(UtenteBean utente) {
        this.currentUser = utente;
    }

    
    public UtenteBean getCurrentUser() {
        return currentUser;
    }
    
    
    public void setCarrelloCorrente(java.util.List<it.ortogest.ortogestapp.beans.RigaOrdineBean> carrello) {
        this.currentCart = carrello;
    }

    public java.util.List<it.ortogest.ortogestapp.beans.RigaOrdineBean> getCarrelloCorrente() {
        return currentCart;
    }

    
    public void logout() {
        this.currentUser = null;
        this.currentCart = null;
    }
}
