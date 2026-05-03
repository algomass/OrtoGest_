package it.ortogest.ortogestapp.beans;

/**
 * DTO per trasportare i dati di una singola riga (voce) dell'ordine.
 * Usato sia per il carrello lato client sia per il riepilogo degli ordini.
 */
public class RigaOrdineBean {
    private String nomeProdotto;
    private double quantita;       // In Kg
    private double prezzoUnitario; // €/Kg

    public RigaOrdineBean(String nomeProdotto, double quantita, double prezzoUnitario) {
        this.nomeProdotto = nomeProdotto;
        this.quantita = quantita;
        this.prezzoUnitario = prezzoUnitario;
    }

    public String getNomeProdotto() { return nomeProdotto; }
    public void setNomeProdotto(String nomeProdotto) { this.nomeProdotto = nomeProdotto; }

    public double getQuantita() { return quantita; }
    public void setQuantita(double quantita) { this.quantita = quantita; }

    public double getPrezzoUnitario() { return prezzoUnitario; }
    public void setPrezzoUnitario(double prezzoUnitario) { this.prezzoUnitario = prezzoUnitario; }

    /**
     * Calcola il subtotale di questa riga.
     */
    public double getSubtotale() {
        return prezzoUnitario * quantita;
    }
}
