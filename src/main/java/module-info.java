module it.ortogest.ortogestapp {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires java.logging;

    
    requires transitive java.sql;
    
    
    requires org.apache.commons.mail;

    
    exports it.ortogest.ortogestapp;

    opens it.ortogest.ortogestapp to javafx.fxml;

    
    exports it.ortogest.ortogestapp.graphiccontroller;

    opens it.ortogest.ortogestapp.graphiccontroller to javafx.fxml;

    
    exports it.ortogest.ortogestapp.beans;
    exports it.ortogest.ortogestapp.model;
    exports it.ortogest.ortogestapp.utils;

    opens it.ortogest.ortogestapp.beans to javafx.base;
    opens it.ortogest.ortogestapp.model to javafx.base;
    opens it.ortogest.ortogestapp.utils to javafx.fxml;
}
