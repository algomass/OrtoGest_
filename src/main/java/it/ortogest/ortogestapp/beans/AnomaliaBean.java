package it.ortogest.ortogestapp.beans;

/**
 * DTO per trasportare i dati di una segnalazione di anomalia 
 * (merce mancante o danneggiata) dal Boundary al Control.
 */
public class AnomaliaBean {
    private String tipoAnomalia; // "Mancante" o "Danneggiata"
    private String nomeProdotto;
    private String lotto;
    private double quantita;
    private String note;
    private String emailFornitore;

    public AnomaliaBean(String tipoAnomalia, String nomeProdotto, String lotto, double quantita, String note, String emailFornitore) {
        this.tipoAnomalia = tipoAnomalia;
        this.nomeProdotto = nomeProdotto;
        this.lotto = lotto;
        this.quantita = quantita;
        this.note = note;
        this.emailFornitore = emailFornitore;
    }

    public String getTipoAnomalia() { return tipoAnomalia; }
    public void setTipoAnomalia(String tipoAnomalia) { this.tipoAnomalia = tipoAnomalia; }

    public String getNomeProdotto() { return nomeProdotto; }
    public void setNomeProdotto(String nomeProdotto) { this.nomeProdotto = nomeProdotto; }

    public String getLotto() { return lotto; }
    public void setLotto(String lotto) { this.lotto = lotto; }

    public double getQuantita() { return quantita; }
    public void setQuantita(double quantita) { this.quantita = quantita; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getEmailFornitore() { return emailFornitore; }
    public void setEmailFornitore(String emailFornitore) { this.emailFornitore = emailFornitore; }
}
