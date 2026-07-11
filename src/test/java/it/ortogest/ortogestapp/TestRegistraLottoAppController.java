package it.ortogest.ortogestapp;

import it.ortogest.ortogestapp.appcontroller.RegistraLottoAppController;
import it.ortogest.ortogestapp.beans.LottoBean;
import it.ortogest.ortogestapp.exception.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class TestRegistraLottoAppController {

    private RegistraLottoAppController controller;

    @BeforeEach
    void setUp() {
        // Inizializza il controller che verrà utilizzato nei test
        controller = RegistraLottoAppController.getInstance();
    }

    @Test
    void testRegistraLotto_IdVuoto() {
        LottoBean bean = new LottoBean();
        bean.setIdLotto("");
        bean.setQuantitaKg(10);
        bean.setDataArrivo(LocalDate.now());
        bean.setDataScadenza(LocalDate.now().plusDays(5));

        Assertions.assertThrows(
                ValidationException.class,
                () -> controller.registraLotto(bean),
                "Doveva essere lanciata ValidationException per ID lotto vuoto"
        );
    }

    @Test
    void testRegistraLotto_QuantitaNegativa() {
        LottoBean bean = new LottoBean();
        bean.setIdLotto("L001");
        bean.setQuantitaKg(-5);
        bean.setDataArrivo(LocalDate.now());
        bean.setDataScadenza(LocalDate.now().plusDays(5));

        Assertions.assertThrows(
                ValidationException.class,
                () -> controller.registraLotto(bean),
                "Doveva essere lanciata ValidationException per quantità negativa"
        );
    }

    @Test
    void testRegistraLotto_DateInvertite() {
        LottoBean bean = new LottoBean();
        bean.setIdLotto("L002");
        bean.setQuantitaKg(10);
        bean.setDataArrivo(LocalDate.now().plusDays(5));
        bean.setDataScadenza(LocalDate.now());

        Assertions.assertThrows(
                ValidationException.class,
                () -> controller.registraLotto(bean),
                "Doveva essere lanciata ValidationException per data di scadenza precedente all'arrivo"
        );
    }
}
