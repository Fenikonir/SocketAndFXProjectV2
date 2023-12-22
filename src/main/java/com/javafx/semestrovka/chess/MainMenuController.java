package com.javafx.semestrovka.chess;

import com.github.alexdlaird.util.StringUtils;
import com.javafx.semestrovka.ChessBoard;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class MainMenuController {
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField numberField;
    @FXML
    private Label lengthLabel;
    @FXML
    private ChoiceBox<String> answerChoiceBox;

    @FXML
    private ImageView imageView;

    @FXML
    private ImageView imageView1;

    @FXML
    private void initialize() {
        // Инициализация ChoiceBox
//        answerChoiceBox.getItems().addAll(
//                "adventurer", "alpha", "berlin", "cardinal", "cases", "cheq", "chess_samara", "chess7",
//                "chess24", "chesscom", "chessnut", "companion", "condal", "dash", "dilena", "dubrovny",
//                "fantasy", "fresca", "glass", "graffiti", "graffiti_light", "kingdom", "kosal", "leipzig",
//                "letter", "lucena", "maestro", "marble", "maya", "mediaeval", "merida", "metro", "pirouetti",
//                "pixel", "reilly", "riohacha", "shapes", "spatial", "staunty", "symbol", "symmetric", "tatiana",
//                "tournament", "tournament_metal", "uscf", "wikipedia"
//        );
        answerChoiceBox.setValue("pixel");

        // Установка слушателя событий для ChoiceBox
        answerChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> updateImages(newValue)
        );

        // Изначальная установка изображений
        updateImages(answerChoiceBox.getValue());
    }

    // Метод для обновления изображений в зависимости от выбранного элемента в ChoiceBox
    private void updateImages(String selectedSkin) {
        if (selectedSkin != null) {
            String basePath = "/com/javafx/semestrovka/";

            // Генерация путей к изображениям в зависимости от выбранного скина
            String whiteImagePath = basePath + selectedSkin + "/wK.png";
            String blackImagePath = basePath + selectedSkin + "/bK.png";

            // Установка новых изображений
            imageView.setImage(new Image(whiteImagePath));
            imageView1.setImage(new Image(blackImagePath));
        }
    }

    @FXML
    private void onFastSearch() {
        String result = getResult(false, 0);
        openChessBoard(result);
    }

    @FXML
    private void onCodeSearch() {
        try {
            int code = Integer.parseInt(numberField.getText());
            String result = getResult(true, code);
            openChessBoard(result);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Некорректный код");
            alert.setHeaderText(null);
            alert.setContentText("Вы ввели некоректный код, попробуйте еще раз");
            alert.showAndWait();
        }
    }

    private String getResult(boolean f, int code) {
        String login = StringUtils.isNotBlank(loginField.getText())? loginField.getText() : "User";
        String password = StringUtils.isNotBlank(passwordField.getText())? passwordField.getText() : "password";
        String isFastSearch = String.valueOf(f);
        return String.format("%s:%s:%s:%s", login, password, isFastSearch, code);
    }

    private void openChessBoard(String result) {
        String skin = answerChoiceBox.getValue();
        Platform.runLater(() -> {
            ChessBoard chessBoard = new ChessBoard(result, skin);
            chessBoard.start(new Stage());
        });
//        Stage stage = (Stage) loginField.getScene().getWindow();
//        stage.close();
    }
}
