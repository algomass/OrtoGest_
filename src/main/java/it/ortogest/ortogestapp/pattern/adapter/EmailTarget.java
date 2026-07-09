package it.ortogest.ortogestapp.pattern.adapter;


public interface EmailTarget {
    
    boolean inviaEmail(String destinatario, String oggetto, String corpo);
}
