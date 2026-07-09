package it.ortogest.ortogestapp.model;


public class Lotto extends AbstractLotto {
    private Prodotto tipologiaProdotto;

    private Lotto(Builder builder) {
        super(builder);
        this.tipologiaProdotto = builder.tipologiaProdotto;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends AbstractLotto.AbstractBuilder<Lotto, Builder> {
        private Prodotto tipologiaProdotto;

        @Override
        protected Builder self() {
            return this;
        }

        public Builder tipologiaProdotto(Prodotto tipologiaProdotto) {
            this.tipologiaProdotto = tipologiaProdotto;
            return this;
        }

        @Override
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

    public boolean isEsaurito() {
        return getQuantitaKg() <= 0.0;
    }

    public boolean isScaduto() {
        return getDataScadenza() != null && java.time.LocalDate.now(java.time.ZoneId.systemDefault()).isAfter(getDataScadenza());
    }

    public StatoLotto getStato() {
        if (isSmaltito()) {
            return StatoLotto.SMALTITO;
        } else if (isRitirato()) {
            return StatoLotto.RITIRATO;
        } else if (isEsaurito()) {
            return StatoLotto.ESAURITO;
        } else if (isScaduto()) {
            return StatoLotto.SCADUTO;
        }
        return StatoLotto.ATTIVO;
    }
}
