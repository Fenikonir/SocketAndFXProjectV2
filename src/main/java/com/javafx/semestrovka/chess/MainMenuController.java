package com.javafx.semestrovka.chess;

import com.javafx.semestrovka.ChessBoard;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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
    private void onFastSearch() {
        String result = getResult(false);
        openChessBoard(result);
    }

    @FXML
    private void onCodeSearch() {
        String result = getResult(true);
        openChessBoard(result);
    }

    private String getResult(boolean f) {
        String login = loginField.getText();
//        String password = PasswordHashingExample.hashPassword(passwordField.getText());
        String password = "";
        String isFastSearch = String.valueOf(f);
        String code = numberField.getText();
        return String.format("%s:%s:%s:%s", login, password, isFastSearch, code);
    }

    private void openChessBoard(String result) {
        Platform.runLater(() -> {
            ChessBoard chessBoard = new ChessBoard(result);
            chessBoard.start(new Stage());
        });
//        Stage stage = (Stage) loginField.getScene().getWindow();
//        stage.close();
    }
}
