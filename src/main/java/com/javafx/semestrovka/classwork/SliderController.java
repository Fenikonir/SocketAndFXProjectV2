package com.javafx.semestrovka.classwork;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

public class SliderController {

    @FXML
    private Slider slider;

    @FXML
    private Label label;

    @FXML
    public void initialize() {
        // Инициализация слушателя изменения значения слайдера
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Обновление метки при изменении значения слайдера
            label.setText(String.valueOf(newValue.intValue()));
        });
    }
}

