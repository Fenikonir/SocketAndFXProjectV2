module com.javafx.semestrovka {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
//    requires jbcrypt;

    opens com.javafx.semestrovka to javafx.fxml;
    exports com.javafx.semestrovka;
    exports com.javafx.semestrovka.classwork;
    exports com.javafx.semestrovka.chess;
    opens com.javafx.semestrovka.classwork to javafx.fxml;
    opens com.javafx.semestrovka.chess;
}