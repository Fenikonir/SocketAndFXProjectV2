package com.javafx.semestrovka.classwork;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class StringLengthController {
    @FXML
    private TextField inputField;

    @FXML
    private Label lengthLabel;

    @FXML
    private void onInputFieldChange() {
        String input = inputField.getText();
        int length = input.length();
        lengthLabel.setText("Length: " + length);
    }
}