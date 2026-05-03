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

    public LottoBean(String idLotto, String nomeFornitore, String nomeProdotto, double quantitaKg, LocalDate dataArrivo, LocalDate dataScadenza, double costoAcquisto) {
        this.idLotto = idLotto;
        this.nomeFornitore = nomeFornitore;
        this.nomeProdotto = nomeProdotto;
        this.quantitaKg = quantitaKg;
        this.dataArrivo = dataArrivo;
        this.dataScadenza = dataScadenza;
        this.costoAcquisto = costoAcquisto;
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
}
