package it.ortogest.ortogestapp.beans;


public class OrdineBean {
    private String idOrdine;
    private String riepilogoProdotti; 
    private double totale;
    private String stato; 
    private String emailCliente;

    public OrdineBean(String idOrdine, String riepilogoProdotti, double totale, String stato) {
        this.idOrdine = idOrdine;
        this.riepilogoProdotti = riepilogoProdotti;
        this.totale = totale;
        this.stato = stato;
    }

    public OrdineBean(String idOrdine, String riepilogoProdotti, double totale, String stato, String emailCliente) {
        this.idOrdine = idOrdine;
        this.riepilogoProdotti = riepilogoProdotti;
        this.totale = totale;
        this.stato = stato;
        this.emailCliente = emailCliente;
    }

    public String getIdOrdine() {
        return idOrdine;
    }

    public String getRiepilogoProdotti() {
        return riepilogoProdotti;
    }

    public double getTotale() {
        return totale;
    }

    public String getStato() {
        return stato;
    }

    public String getEmailCliente() {
        return emailCliente;
    }
}
