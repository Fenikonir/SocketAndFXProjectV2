module com.javafx.semestrovka {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;

    opens com.javafx.semestrovka to javafx.fxml;
    exports com.javafx.semestrovka;
}