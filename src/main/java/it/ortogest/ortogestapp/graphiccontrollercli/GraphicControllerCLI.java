package it.ortogest.ortogestapp.graphiccontrollercli;

import java.util.Scanner;

/**
 * Interfaccia comune per tutti i Graphic Controller (Boundary)
 * della versione Command Line Interface (CLI).
 */
public interface GraphicControllerCLI {
    
    /**
     * Avvia il ciclo di esecuzione della vista.
     * @param scanner Lo Scanner condiviso per la lettura dell'input.
     */
    void start(Scanner scanner);
}
