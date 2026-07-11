package it.ortogest.ortogestapp;

import it.ortogest.ortogestapp.appcontroller.CreaOrdineAppController;
import it.ortogest.ortogestapp.exception.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class TestCreaOrdineAppController {

    private CreaOrdineAppController controller;

    @BeforeEach
    void setUp() {
        controller = CreaOrdineAppController.getInstance();
    }

    @Test
    void testCreaOrdine_CarrelloVuoto() {
        Assertions.assertThrows(
                ValidationException.class,
                () -> controller.creaOrdine("test@cliente.it", new ArrayList<>()),
                "Doveva essere lanciata ValidationException per carrello vuoto"
        );
    }

    @Test
    void testCreaOrdine_CarrelloNull() {
        Assertions.assertThrows(
                ValidationException.class,
                () -> controller.creaOrdine("test@cliente.it", null),
                "Doveva essere lanciata ValidationException per carrello null"
        );
    }
}
