package it.ortogest.ortogestapp.model;

/**
 * Entità di Dominio: Rappresenta una singola riga (voce) di un Ordine.
 * Ogni riga descrive un prodotto ordinato con la sua quantità e prezzo unitario.
 */
public class RigaOrdine {
    private String nomeProdotto;
    private double quantita;       // In Kg
    private double prezzoUnitario; // €/Kg al momento dell'ordine

    public RigaOrdine(String nomeProdotto, double quantita, double prezzoUnitario) {
        this.nomeProdotto = nomeProdotto;
        this.quantita = quantita;
        this.prezzoUnitario = prezzoUnitario;
    }

    public String getNomeProdotto() { return nomeProdotto; }
    public double getQuantita() { return quantita; }
    public double getPrezzoUnitario() { return prezzoUnitario; }

    /**
     * Calcola il subtotale di questa riga (prezzo * quantità).
     */
    public double getSubtotale() {
        return prezzoUnitario * quantita;
    }
}
