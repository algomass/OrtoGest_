package it.ortogest.ortogestapp;

import it.ortogest.ortogestapp.beans.UtenteBean;
import it.ortogest.ortogestapp.utils.SessionManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TestSessionManager {

    @Test
    void testSingletonInstance() {
        SessionManager instance1 = SessionManager.getInstance();
        SessionManager instance2 = SessionManager.getInstance();
        Assertions.assertSame(instance1, instance2, "Le istanze del SessionManager devono essere la stessa (Singleton)");
    }

    @Test
    void testLoginLogout() {
        SessionManager session = SessionManager.getInstance();
        UtenteBean utente = new UtenteBean("Mario", "mario@email.com", "Cliente");
        
        session.login(utente);
        Assertions.assertEquals("mario@email.com", session.getCurrentUser().getEmail(), "L'utente loggato deve corrispondere");
        
        session.logout();
        Assertions.assertNull(session.getCurrentUser(), "Dopo il logout l'utente corrente deve essere null");
    }
}
