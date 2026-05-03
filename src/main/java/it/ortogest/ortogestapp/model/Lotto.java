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

    public Lotto(String idLotto, String nomeFornitore, Prodotto tipologiaProdotto, double quantitaKg, LocalDate dataArrivo, LocalDate dataScadenza, double costoAcquisto) {
        this.idLotto = idLotto;
        this.nomeFornitore = nomeFornitore;
        this.tipologiaProdotto = tipologiaProdotto;
        this.quantitaKg = quantitaKg;
        this.dataArrivo = dataArrivo;
        this.dataScadenza = dataScadenza;
        this.costoAcquisto = costoAcquisto;
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
}
