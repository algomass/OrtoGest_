package it.ortogest.ortogestapp.beans;

/**
 * UtenteBean rappresenta l'utente loggato nel sistema.
 * Segue il pattern JavaBean: incapsula i dati e fornisce i metodi di accesso (getter/setter).
 * Questa classe ha l'unica responsabilitÃƒÆ’Ã‚Â  di trasportare i dati dell'utente, 
 * isolandoli dalla logica di business e dalla UI.
 */
public class UtenteBean {
    private String nome;
    private String email;
    private String ruolo; // es. Magazziniere, Responsabile, Operatore, Cliente

    public UtenteBean(String nome, String email, String ruolo) {
        this.nome = nome;
        this.email = email;
        this.ruolo = ruolo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRuolo() {
        return ruolo;
    }

    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }
}
