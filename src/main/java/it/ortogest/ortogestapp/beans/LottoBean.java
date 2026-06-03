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
        super(builder);
        this.nomeProdotto = builder.nomeProdotto;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends AbstractLotto.AbstractBuilder<LottoBean, Builder> {
        private String nomeProdotto;

        @Override
        protected Builder self() {
            return this;
        }

        public Builder nomeProdotto(String nomeProdotto) { 
            this.nomeProdotto = nomeProdotto; 
            return this; 
        }

        @Override
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
