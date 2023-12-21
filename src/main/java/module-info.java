module com.javafx.semestrovka {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires com.google.auth.oauth2;
    requires com.google.gson;
    requires java.net.http;
    requires com.github.alexdlaird.ngrok;

    opens com.javafx.semestrovka to javafx.fxml;
    exports com.javafx.semestrovka;
    exports com.javafx.semestrovka.chess;
    opens com.javafx.semestrovka.chess;
}