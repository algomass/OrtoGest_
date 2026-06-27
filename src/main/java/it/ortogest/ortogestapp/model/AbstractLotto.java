package it.ortogest.ortogestapp.model;

import java.time.LocalDate;

public abstract class AbstractLotto {
    private String idLotto;
    private String nomeFornitore;
    private double quantitaKg;
    private LocalDate dataArrivo;
    private LocalDate dataScadenza;
    private double costoAcquisto;
    private double prezzoVendita;
    private boolean scontoScadenzaAttivo;
    private double prezzoScontato;
    private boolean smaltito;
    private boolean ritirato;

    protected AbstractLotto() {}

    protected AbstractLotto(AbstractBuilder<?, ?> builder) {
        this.idLotto = builder.idLotto;
        this.nomeFornitore = builder.nomeFornitore;
        this.quantitaKg = builder.quantitaKg;
        this.dataArrivo = builder.dataArrivo;
        this.dataScadenza = builder.dataScadenza;
        this.costoAcquisto = builder.costoAcquisto;
        this.prezzoVendita = builder.prezzoVendita;
        this.scontoScadenzaAttivo = builder.scontoScadenzaAttivo;
        this.prezzoScontato = builder.prezzoScontato;
        this.smaltito = builder.smaltito;
        this.ritirato = builder.ritirato;
    }

    public abstract static class AbstractBuilder<T extends AbstractLotto, B extends AbstractBuilder<T, B>> {
        protected String idLotto;
        protected String nomeFornitore;
        protected double quantitaKg;
        protected LocalDate dataArrivo;
        protected LocalDate dataScadenza;
        protected double costoAcquisto;
        protected double prezzoVendita;
        protected boolean scontoScadenzaAttivo;
        protected double prezzoScontato;
        protected boolean smaltito;
        protected boolean ritirato;

        protected abstract B self();

        public abstract T build();

        public B idLotto(String idLotto) { this.idLotto = idLotto; return self(); }
        public B nomeFornitore(String nomeFornitore) { this.nomeFornitore = nomeFornitore; return self(); }
        public B quantitaKg(double quantitaKg) { this.quantitaKg = quantitaKg; return self(); }
        public B dataArrivo(LocalDate dataArrivo) { this.dataArrivo = dataArrivo; return self(); }
        public B dataScadenza(LocalDate dataScadenza) { this.dataScadenza = dataScadenza; return self(); }
        public B costoAcquisto(double costoAcquisto) { this.costoAcquisto = costoAcquisto; return self(); }
        public B prezzoVendita(double prezzoVendita) { this.prezzoVendita = prezzoVendita; return self(); }
        public B scontoScadenzaAttivo(boolean scontoScadenzaAttivo) { this.scontoScadenzaAttivo = scontoScadenzaAttivo; return self(); }
        public B prezzoScontato(double prezzoScontato) { this.prezzoScontato = prezzoScontato; return self(); }
        public B smaltito(boolean smaltito) { this.smaltito = smaltito; return self(); }
        public B ritirato(boolean ritirato) { this.ritirato = ritirato; return self(); }
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

    public boolean isSmaltito() {
        return smaltito;
    }

    public void setSmaltito(boolean smaltito) {
        this.smaltito = smaltito;
    }

    public boolean isRitirato() {
        return ritirato;
    }

    public void setRitirato(boolean ritirato) {
        this.ritirato = ritirato;
    }
}
