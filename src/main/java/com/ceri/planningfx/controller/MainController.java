package com.ceri.planningfx.controller;

import com.ceri.planningfx.utilities.HeaderManager;
import javafx.fxml.FXML;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;

public class MainController {
    @FXML
    private BorderPane mainBorderPane;

    public void initialize() {
        System.out.println("MainController initialize");
        HeaderManager.setMainBorderPane(mainBorderPane);
        if (HeaderManager.getMainBorderPane() == null) {
            System.out.println("MainController initialize: mainBorderPane is null");
        }
    }
}
