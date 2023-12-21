package com.javafx.semestrovka;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("chess/start-page.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 350, 300);
        stage.setTitle("Шахматы: Стартовое меню");
        stage.setScene(scene);
        stage.getIcons().add(new Image("/com/javafx/semestrovka/chess/chess_pawn_game_icon_231432.png"));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
