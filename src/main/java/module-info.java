module it.ortogest.ortogestapp {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires java.logging;

    // Se usi JDBC per il database, decommenta la riga sotto:
    // requires java.sql;

    // 1. Apri ed esporta il package root (dove si trova AppStarter)
    exports it.ortogest.ortogestapp;

    opens it.ortogest.ortogestapp to javafx.fxml;

    // 2. Apri ed esporta i package dei controller
    exports it.ortogest.ortogestapp.graphiccontroller;

    opens it.ortogest.ortogestapp.graphiccontroller to javafx.fxml;

    // 3. Esporta i package utilizzati nelle firme pubbliche esportate
    exports it.ortogest.ortogestapp.beans;
    exports it.ortogest.ortogestapp.utils;

    opens it.ortogest.ortogestapp.utils to javafx.fxml;
}
