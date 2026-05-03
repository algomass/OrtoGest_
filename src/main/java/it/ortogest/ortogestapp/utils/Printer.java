package it.ortogest.ortogestapp.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Printer {

    private static final Logger LOGGER = Logger.getLogger(Printer.class.getName());

    private Printer () {}

    public static void printf(String s) {
        printCLI(String.format("%s%n", s));
    }

    public static void print(String s) {
        printCLI(s);
    }

    public static void perror(String s){
        printCLI("\033[31m" + s + "\033[0m" + "\n");
    }

    private static void printCLI(String s) {
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info(s);
        }
    }

}
