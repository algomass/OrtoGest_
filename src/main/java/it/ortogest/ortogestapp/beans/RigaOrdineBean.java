package it.ortogest.ortogestapp.beans;

/**
 * DTO per trasportare i dati di una singola riga (voce) dell'ordine.
 * Usato sia per il carrello lato client sia per il riepilogo degli ordini.
 */
public class RigaOrdineBean {
    private String nomeProdotto;
    private String idLotto;
    private double quantita;       // In Kg
    private double prezzoUnitario; // ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬/Kg

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

    /**
     * Calcola il subtotale di questa riga.
     */
    public double getSubtotale() {
        return prezzoUnitario * quantita;
    }
}
