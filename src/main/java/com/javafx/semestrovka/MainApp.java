package com.javafx.semestrovka;

import com.javafx.semestrovka.classwork.SliderApplication;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("chess/start-page.fxml"));
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("chess/start-page.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 350, 205);
        stage.setTitle("Шахаты: Стартовое меню");
        stage.setScene(scene);

//        scene.widthProperty().addListener((observableValue, oldWidth, newWidth) -> {
//            System.out.println("Ширина окна: " + newWidth);
//        });
//
//        scene.heightProperty().addListener((observableValue, oldHeight, newHeight) -> {
//            System.out.println("Высота окна: " + newHeight);
//        });

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
