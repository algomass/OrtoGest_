package it.ortogest.ortogestapp.model;


public class Utente {
    private String nome;
    private String email;
    private String password;
    private String ruolo;

    public Utente(String nome, String email, String password, String ruolo) {
        this.nome = nome;
        this.email = email;
        this.password = password;
        this.ruolo = ruolo;
    }

    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRuolo() { return ruolo; }
}
