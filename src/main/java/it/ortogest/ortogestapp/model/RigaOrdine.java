package it.ortogest.ortogestapp.model;

/**
 * EntitÃƒÂ  di Dominio: Rappresenta una singola riga (voce) di un Ordine.
 * Ogni riga descrive un prodotto ordinato con la sua quantitÃƒÂ  e prezzo unitario.
 */
public class RigaOrdine {
    private String nomeProdotto;
    private String idLotto;
    private double quantita;       // In Kg
    private double prezzoUnitario; // Ã¢â€šÂ¬/Kg al momento dell'ordine

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

    /**
     * Calcola il subtotale di questa riga (prezzo * quantitÃƒÂ ).
     */
    public double getSubtotale() {
        return prezzoUnitario * quantita;
    }
}
