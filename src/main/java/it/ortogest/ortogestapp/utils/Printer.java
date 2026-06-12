package it.ortogest.ortogestapp.utils;

public class Printer {

    private Printer() {
    }

    public static void printf(String format, Object... args) {
        System.out.print(String.format(format, args));
    }

    public static void print(String s) {
        System.out.println(s);
    }

    public static void perror(String s) {
        System.out.println("\033[31m" + s + "\033[0m");
    }
}
