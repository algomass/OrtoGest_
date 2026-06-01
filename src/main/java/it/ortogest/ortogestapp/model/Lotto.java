package it.ortogest.ortogestapp.model;

import java.time.LocalDate;

/**
 * Entità di Dominio: Rappresenta uno specifico carico di merce in entrata.
 * Raccoglie i dati fisici di una consegna (lotto) e si associa alla tipologia di Prodotto.
 */
public class Lotto {
    private String idLotto; // Codice inserito manualmente dal magazziniere leggendo il bancale
    private String nomeFornitore;
    private Prodotto tipologiaProdotto;
    private double quantitaKg;
    private LocalDate dataArrivo;
    private LocalDate dataScadenza;
    private double costoAcquisto; // Costo di acquisto del lotto dal fornitore
    private double prezzoVendita;
    private boolean scontoScadenzaAttivo;
    private double prezzoScontato;

    private Lotto(Builder builder) {
        this.idLotto = builder.idLotto;
        this.nomeFornitore = builder.nomeFornitore;
        this.tipologiaProdotto = builder.tipologiaProdotto;
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
        private Prodotto tipologiaProdotto;
        private double quantitaKg;
        private LocalDate dataArrivo;
        private LocalDate dataScadenza;
        private double costoAcquisto;
        private double prezzoVendita;
        private boolean scontoScadenzaAttivo;
        private double prezzoScontato;

        public Builder idLotto(String idLotto) { this.idLotto = idLotto; return this; }
        public Builder nomeFornitore(String nomeFornitore) { this.nomeFornitore = nomeFornitore; return this; }
        public Builder tipologiaProdotto(Prodotto tipologiaProdotto) { this.tipologiaProdotto = tipologiaProdotto; return this; }
        public Builder quantitaKg(double quantitaKg) { this.quantitaKg = quantitaKg; return this; }
        public Builder dataArrivo(LocalDate dataArrivo) { this.dataArrivo = dataArrivo; return this; }
        public Builder dataScadenza(LocalDate dataScadenza) { this.dataScadenza = dataScadenza; return this; }
        public Builder costoAcquisto(double costoAcquisto) { this.costoAcquisto = costoAcquisto; return this; }
        public Builder prezzoVendita(double prezzoVendita) { this.prezzoVendita = prezzoVendita; return this; }
        public Builder scontoScadenzaAttivo(boolean scontoScadenzaAttivo) { this.scontoScadenzaAttivo = scontoScadenzaAttivo; return this; }
        public Builder prezzoScontato(double prezzoScontato) { this.prezzoScontato = prezzoScontato; return this; }

        public Lotto build() {
            return new Lotto(this);
        }
    }

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

    public Prodotto getTipologiaProdotto() {
        return tipologiaProdotto;
    }

    public void setTipologiaProdotto(Prodotto tipologiaProdotto) {
        this.tipologiaProdotto = tipologiaProdotto;
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
