package it.ortogest.ortogestapp.model;


public class Prodotto {
    private String nome;
    private double prezzoAttuale;
    private double quantitaTotaleDisponibile; 
    private String categoria; 

    public Prodotto(String nome, double prezzoAttuale, double quantitaTotaleDisponibile, String categoria) {
        this.nome = nome;
        this.prezzoAttuale = prezzoAttuale;
        this.quantitaTotaleDisponibile = quantitaTotaleDisponibile;
        this.categoria = categoria;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public double getPrezzoAttuale() { return prezzoAttuale; }
    public void setPrezzoAttuale(double prezzoAttuale) { this.prezzoAttuale = prezzoAttuale; }

    public double getQuantitaTotaleDisponibile() { return quantitaTotaleDisponibile; }
    public void setQuantitaTotaleDisponibile(double q) { this.quantitaTotaleDisponibile = q; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public void sottraiGiacenza(double quantita) {
        this.quantitaTotaleDisponibile -= quantita;
    }

    public void aggiungiGiacenza(double quantita) {
        this.quantitaTotaleDisponibile += quantita;
    }
}
