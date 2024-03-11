package com.ceri.planningfx.utilities;

import javafx.scene.layout.BorderPane;

public class HeaderManager {
    private static BorderPane mainBorderPane;

    public static BorderPane getMainBorderPane() {
        return mainBorderPane;
    }

    public static void setMainBorderPane(BorderPane borderPane) {
        mainBorderPane = borderPane;
    }
}
