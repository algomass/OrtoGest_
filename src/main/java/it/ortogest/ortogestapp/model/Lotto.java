package it.ortogest.ortogestapp.model;

import java.time.LocalDate;

/**
 * Entità di Dominio: Rappresenta uno specifico carico di merce in entrata.
 * Raccoglie i dati fisici di una consegna (lotto) e si associa alla tipologia di Prodotto.
 */
public class Lotto extends AbstractLotto {
    private Prodotto tipologiaProdotto;

    private Lotto(Builder builder) {
        super();
        this.setIdLotto(builder.idLotto);
        this.setNomeFornitore(builder.nomeFornitore);
        this.tipologiaProdotto = builder.tipologiaProdotto;
        this.setQuantitaKg(builder.quantitaKg);
        this.setDataArrivo(builder.dataArrivo);
        this.setDataScadenza(builder.dataScadenza);
        this.setCostoAcquisto(builder.costoAcquisto);
        this.setPrezzoVendita(builder.prezzoVendita);
        this.setScontoScadenzaAttivo(builder.scontoScadenzaAttivo);
        this.setPrezzoScontato(builder.prezzoScontato);
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

    public Prodotto getTipologiaProdotto() {
        return tipologiaProdotto;
    }

    public void setTipologiaProdotto(Prodotto tipologiaProdotto) {
        this.tipologiaProdotto = tipologiaProdotto;
    }
}
