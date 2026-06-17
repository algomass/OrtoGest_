package it.ortogest.ortogestapp.model;

/**
 * EntitÃƒÆ’Ã‚Â  di Dominio: Rappresenta uno specifico carico di merce in entrata.
 * Raccoglie i dati fisici di una consegna (lotto) e si associa alla tipologia
 * di Prodotto.
 */
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
}
