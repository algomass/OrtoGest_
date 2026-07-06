package it.ortogest.ortogestapp;

import it.ortogest.ortogestapp.beans.LottoBean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class TestLottoBean {

    @Test
    void testBuilderLottoBean() {
        LocalDate arrivo = LocalDate.now();
        LocalDate scadenza = arrivo.plusDays(10);
        
        LottoBean bean = LottoBean.builder()
                .idLotto("L-123")
                .nomeFornitore("FornitoreTest")
                .nomeProdotto("Mela")
                .quantitaKg(20.5)
                .dataArrivo(arrivo)
                .dataScadenza(scadenza)
                .costoAcquisto(1.5)
                .build();
                
        Assertions.assertEquals("L-123", bean.getIdLotto(), "L'ID Lotto deve essere impostato correttamente");
        Assertions.assertEquals("FornitoreTest", bean.getNomeFornitore(), "Il nome fornitore deve essere impostato correttamente");
        Assertions.assertEquals("Mela", bean.getNomeProdotto(), "Il nome prodotto deve essere impostato correttamente");
        Assertions.assertEquals(20.5, bean.getQuantitaKg(), "La quantità deve essere impostata correttamente");
        Assertions.assertEquals(arrivo, bean.getDataArrivo(), "La data di arrivo deve essere impostata correttamente");
        Assertions.assertEquals(scadenza, bean.getDataScadenza(), "La data di scadenza deve essere impostata correttamente");
        Assertions.assertEquals(1.5, bean.getCostoAcquisto(), "Il costo di acquisto deve essere impostato correttamente");
    }
    
    @Test
    void testSettersLottoBean() {
        LottoBean bean = new LottoBean();
        bean.setIdLotto("L-999");
        bean.setNomeProdotto("Pera");
        
        Assertions.assertEquals("L-999", bean.getIdLotto());
        Assertions.assertEquals("Pera", bean.getNomeProdotto());
    }
}
