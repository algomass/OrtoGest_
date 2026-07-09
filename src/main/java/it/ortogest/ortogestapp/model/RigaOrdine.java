package it.ortogest.ortogestapp.model;


public class RigaOrdine {
    private String nomeProdotto;
    private String idLotto;
    private double quantita;       
    private double prezzoUnitario; 

    public RigaOrdine(String nomeProdotto, String idLotto, double quantita, double prezzoUnitario) {
        this.nomeProdotto = nomeProdotto;
        this.idLotto = idLotto;
        this.quantita = quantita;
        this.prezzoUnitario = prezzoUnitario;
    }

    public String getNomeProdotto() { return nomeProdotto; }
    public String getIdLotto() { return idLotto; }
    public double getQuantita() { return quantita; }
    public double getPrezzoUnitario() { return prezzoUnitario; }

    
    public double getSubtotale() {
        return prezzoUnitario * quantita;
    }
}
