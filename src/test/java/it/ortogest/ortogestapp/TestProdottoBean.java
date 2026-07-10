package it.ortogest.ortogestapp;

import it.ortogest.ortogestapp.beans.ProdottoBean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TestProdottoBean {

    @Test
    void testCostruttoreEGetters() {
        ProdottoBean bean = new ProdottoBean("Fragola", 3.5, 50.0, "Frutta");
        
        Assertions.assertEquals("Fragola", bean.getNome(), "Il nome deve corrispondere");
        Assertions.assertEquals(3.5, bean.getPrezzoAttuale(), "Il prezzo deve corrispondere");
        Assertions.assertEquals(50.0, bean.getGiacenza(), "La giacenza deve corrispondere");
        Assertions.assertEquals("Frutta", bean.getCategoria(), "La categoria deve corrispondere");
    }
    
    @Test
    void testCampiAggiuntivi() {
        ProdottoBean bean = new ProdottoBean();
        bean.setInOffertaScadenza(true);
        bean.setPrezzoMin(1.0);
        bean.setPrezzoMax(2.5);
        
        Assertions.assertTrue(bean.isInOffertaScadenza(), "Deve risultare in offerta");
        Assertions.assertEquals(1.0, bean.getPrezzoMin());
        Assertions.assertEquals(2.5, bean.getPrezzoMax());
    }
}
