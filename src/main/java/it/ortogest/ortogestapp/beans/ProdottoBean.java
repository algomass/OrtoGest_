package it.ortogest.ortogestapp.beans;

/**
 * DTO per trasportare i dati del Prodotto dal backend alla vista (Catalogo).
 */
public class ProdottoBean {
    private String nome;
    private double prezzoAttuale;
    private double giacenza;
    private String categoria;
    private String immaginePath;
    private double prezzoAcquistoMedio;

    public ProdottoBean(String nome, double prezzoAttuale, double giacenza, String categoria, String immaginePath) {
        this.nome = nome;
        this.prezzoAttuale = prezzoAttuale;
        this.giacenza = giacenza;
        this.categoria = categoria;
        this.immaginePath = immaginePath;
    }

    public ProdottoBean() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getPrezzoAttuale() {
        return prezzoAttuale;
    }

    public void setPrezzoAttuale(double prezzoAttuale) {
        this.prezzoAttuale = prezzoAttuale;
    }

    public double getGiacenza() {
        return giacenza;
    }

    public void setGiacenza(double giacenza) {
        this.giacenza = giacenza;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getImmaginePath() {
        return immaginePath;
    }

    public void setImmaginePath(String immaginePath) {
        this.immaginePath = immaginePath;
    }

    public double getPrezzoAcquistoMedio() {
        return prezzoAcquistoMedio;
    }

    public void setPrezzoAcquistoMedio(double prezzoAcquistoMedio) {
        this.prezzoAcquistoMedio = prezzoAcquistoMedio;
    }
}
