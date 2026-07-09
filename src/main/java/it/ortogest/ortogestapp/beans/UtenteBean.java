package it.ortogest.ortogestapp.beans;


public class UtenteBean {
    private String nome;
    private String email;
    private String ruolo; 

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
