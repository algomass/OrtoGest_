package it.ortogest.ortogestapp.model;

/**
 * EntitÃƒÆ’Ã‚Â  di Dominio: Rappresenta un prodotto presente nel sistema.
 */
public class Prodotto {
    private String nome;
    private double prezzoAttuale;
    private double quantitaTotaleDisponibile; // In Kg
    private String categoria; // "Frutta" o "Verdura"
    private String immaginePath; // Path relativo nel classpath (es. "/images/mele_golden.png")

    public Prodotto(String nome, double prezzoAttuale, double quantitaTotaleDisponibile, String categoria, String immaginePath) {
        this.nome = nome;
        this.prezzoAttuale = prezzoAttuale;
        this.quantitaTotaleDisponibile = quantitaTotaleDisponibile;
        this.categoria = categoria;
        this.immaginePath = immaginePath;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public double getPrezzoAttuale() { return prezzoAttuale; }
    public void setPrezzoAttuale(double prezzoAttuale) { this.prezzoAttuale = prezzoAttuale; }

    public double getQuantitaTotaleDisponibile() { return quantitaTotaleDisponibile; }
    public void setQuantitaTotaleDisponibile(double q) { this.quantitaTotaleDisponibile = q; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getImmaginePath() { return immaginePath; }
    public void setImmaginePath(String immaginePath) { this.immaginePath = immaginePath; }

    public void sottraiGiacenza(double quantita) {
        this.quantitaTotaleDisponibile -= quantita;
    }

    public void aggiungiGiacenza(double quantita) {
        this.quantitaTotaleDisponibile += quantita;
    }
}
