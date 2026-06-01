package it.ortogest.ortogestapp.beans;

import java.time.LocalDate;

/**
 * Data Transfer Object per trasportare i dati del Lotto
 * tra il Graphic Controller e l'Application Controller.
 */
public class LottoBean {
    private String idLotto;
    private String nomeFornitore;
    private String nomeProdotto; // Trasportiamo solo il nome del prodotto (stringa) per disaccoppiamento
    private double quantitaKg;
    private LocalDate dataArrivo;
    private LocalDate dataScadenza;
    private double costoAcquisto;
    private double prezzoVendita;
    private boolean scontoScadenzaAttivo;
    private double prezzoScontato;

    private LottoBean(Builder builder) {
        this.idLotto = builder.idLotto;
        this.nomeFornitore = builder.nomeFornitore;
        this.nomeProdotto = builder.nomeProdotto;
        this.quantitaKg = builder.quantitaKg;
        this.dataArrivo = builder.dataArrivo;
        this.dataScadenza = builder.dataScadenza;
        this.costoAcquisto = builder.costoAcquisto;
        this.prezzoVendita = builder.prezzoVendita;
        this.scontoScadenzaAttivo = builder.scontoScadenzaAttivo;
        this.prezzoScontato = builder.prezzoScontato;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String idLotto;
        private String nomeFornitore;
        private String nomeProdotto;
        private double quantitaKg;
        private LocalDate dataArrivo;
        private LocalDate dataScadenza;
        private double costoAcquisto;
        private double prezzoVendita;
        private boolean scontoScadenzaAttivo;
        private double prezzoScontato;

        public Builder idLotto(String idLotto) { this.idLotto = idLotto; return this; }
        public Builder nomeFornitore(String nomeFornitore) { this.nomeFornitore = nomeFornitore; return this; }
        public Builder nomeProdotto(String nomeProdotto) { this.nomeProdotto = nomeProdotto; return this; }
        public Builder quantitaKg(double quantitaKg) { this.quantitaKg = quantitaKg; return this; }
        public Builder dataArrivo(LocalDate dataArrivo) { this.dataArrivo = dataArrivo; return this; }
        public Builder dataScadenza(LocalDate dataScadenza) { this.dataScadenza = dataScadenza; return this; }
        public Builder costoAcquisto(double costoAcquisto) { this.costoAcquisto = costoAcquisto; return this; }
        public Builder prezzoVendita(double prezzoVendita) { this.prezzoVendita = prezzoVendita; return this; }
        public Builder scontoScadenzaAttivo(boolean scontoScadenzaAttivo) { this.scontoScadenzaAttivo = scontoScadenzaAttivo; return this; }
        public Builder prezzoScontato(double prezzoScontato) { this.prezzoScontato = prezzoScontato; return this; }

        public LottoBean build() {
            return new LottoBean(this);
        }
    }

    public LottoBean() {}

    public String getIdLotto() {
        return idLotto;
    }

    public void setIdLotto(String idLotto) {
        this.idLotto = idLotto;
    }

    public String getNomeFornitore() {
        return nomeFornitore;
    }

    public void setNomeFornitore(String nomeFornitore) {
        this.nomeFornitore = nomeFornitore;
    }

    public String getNomeProdotto() {
        return nomeProdotto;
    }

    public void setNomeProdotto(String nomeProdotto) {
        this.nomeProdotto = nomeProdotto;
    }

    public double getQuantitaKg() {
        return quantitaKg;
    }

    public void setQuantitaKg(double quantitaKg) {
        this.quantitaKg = quantitaKg;
    }

    public LocalDate getDataArrivo() {
        return dataArrivo;
    }

    public void setDataArrivo(LocalDate dataArrivo) {
        this.dataArrivo = dataArrivo;
    }

    public LocalDate getDataScadenza() {
        return dataScadenza;
    }

    public void setDataScadenza(LocalDate dataScadenza) {
        this.dataScadenza = dataScadenza;
    }

    public double getCostoAcquisto() {
        return costoAcquisto;
    }

    public void setCostoAcquisto(double costoAcquisto) {
        this.costoAcquisto = costoAcquisto;
    }

    public double getPrezzoVendita() {
        return prezzoVendita;
    }

    public void setPrezzoVendita(double prezzoVendita) {
        this.prezzoVendita = prezzoVendita;
    }

    public boolean isScontoScadenzaAttivo() {
        return scontoScadenzaAttivo;
    }

    public void setScontoScadenzaAttivo(boolean scontoScadenzaAttivo) {
        this.scontoScadenzaAttivo = scontoScadenzaAttivo;
    }

    public double getPrezzoScontato() {
        return prezzoScontato;
    }

    public void setPrezzoScontato(double prezzoScontato) {
        this.prezzoScontato = prezzoScontato;
    }
}
