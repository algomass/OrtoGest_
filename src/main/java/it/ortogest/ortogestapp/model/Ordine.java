package it.ortogest.ortogestapp.model;

import java.util.ArrayList;
import java.util.List;


public class Ordine {
    private String idOrdine;
    private String emailCliente;
    private List<RigaOrdine> righe;
    private double totale;
    private String stato; 

    public Ordine(String idOrdine, String emailCliente, List<RigaOrdine> righe, String stato) {
        this.idOrdine = idOrdine;
        this.emailCliente = emailCliente;
        this.righe = new ArrayList<>(righe);
        this.stato = stato;
        
        this.totale = 0;
        for (RigaOrdine r : righe) {
            this.totale += r.getSubtotale();
        }
    }

    public String getIdOrdine() { return idOrdine; }
    public String getEmailCliente() { return emailCliente; }
    public List<RigaOrdine> getRighe() { return righe; }
    public double getTotale() { return totale; }
    public String getStato() { return stato; }

    public void setStato(String stato) { this.stato = stato; }

    
    public String getRiepilogoProdotti() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < righe.size(); i++) {
            RigaOrdine r = righe.get(i);
            sb.append(r.getNomeProdotto())
              .append(" x")
              .append(String.format("%.1f", r.getQuantita()))
              .append("kg");
            if (i < righe.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
