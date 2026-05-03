package it.ortogest.ortogestapp.beans;

/**
 * DTO per trasportare i dati di un Ordine dal backend alla vista
 * (Tracciamento).
 * Ogni ordine contiene una o più righe (prodotti ordinati).
 */
public class OrdineBean {
    private String idOrdine;
    private String riepilogoProdotti; // Es: "Mele Golden x2kg, Banane x1.5kg"
    private double totale;
    private String stato; // "Inviato", "Pronto per il Ritiro", "Ritirato"

    public OrdineBean(String idOrdine, String riepilogoProdotti, double totale, String stato) {
        this.idOrdine = idOrdine;
        this.riepilogoProdotti = riepilogoProdotti;
        this.totale = totale;
        this.stato = stato;
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
}
