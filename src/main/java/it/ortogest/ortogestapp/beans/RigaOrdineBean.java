package it.ortogest.ortogestapp.beans;


public class RigaOrdineBean {
    private String nomeProdotto;
    private String idLotto;
    private double quantita;       
    private double prezzoUnitario; 

    public RigaOrdineBean(String nomeProdotto, String idLotto, double quantita, double prezzoUnitario) {
        this.nomeProdotto = nomeProdotto;
        this.idLotto = idLotto;
        this.quantita = quantita;
        this.prezzoUnitario = prezzoUnitario;
    }

    public String getNomeProdotto() { return nomeProdotto; }
    public void setNomeProdotto(String nomeProdotto) { this.nomeProdotto = nomeProdotto; }

    public String getIdLotto() { return idLotto; }
    public void setIdLotto(String idLotto) { this.idLotto = idLotto; }

    public double getQuantita() { return quantita; }
    public void setQuantita(double quantita) { this.quantita = quantita; }

    public double getPrezzoUnitario() { return prezzoUnitario; }
    public void setPrezzoUnitario(double prezzoUnitario) { this.prezzoUnitario = prezzoUnitario; }

    
    public double getSubtotale() {
        return prezzoUnitario * quantita;
    }
}
