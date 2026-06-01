package it.ortogest.ortogestapp.beans;

import it.ortogest.ortogestapp.model.AbstractLotto;
import java.time.LocalDate;

/**
 * Data Transfer Object per trasportare i dati del Lotto
 * tra il Graphic Controller e l'Application Controller.
 */
public class LottoBean extends AbstractLotto {
    private String nomeProdotto; // Trasportiamo solo il nome del prodotto (stringa) per disaccoppiamento

    private LottoBean(Builder builder) {
        super();
        this.setIdLotto(builder.idLotto);
        this.setNomeFornitore(builder.nomeFornitore);
        this.nomeProdotto = builder.nomeProdotto;
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

    public String getNomeProdotto() {
        return nomeProdotto;
    }

    public void setNomeProdotto(String nomeProdotto) {
        this.nomeProdotto = nomeProdotto;
    }
}
