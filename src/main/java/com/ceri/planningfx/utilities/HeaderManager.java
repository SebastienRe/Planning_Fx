package com.ceri.planningfx.utilities;

import com.ceri.planningfx.controller.EdtController;
import com.ceri.planningfx.controller.FiltresController;
import javafx.scene.layout.BorderPane;

public class HeaderManager {
    private static BorderPane mainBorderPane;
    private static EdtController edtController;
    private static FiltresController filtresController;

    public static EdtController getEdtController() {
        return edtController;
    }

    public static void setEdtController(EdtController controller) {
        edtController = controller;
    }

    public static FiltresController getFiltresController() {
        return filtresController;
    }

    public static void setFiltresController(FiltresController controller) {
        filtresController = controller;
    }

    public static BorderPane getMainBorderPane() {
        return mainBorderPane;
    }

    public static void setMainBorderPane(BorderPane borderPane) {
        mainBorderPane = borderPane;
    }
}
