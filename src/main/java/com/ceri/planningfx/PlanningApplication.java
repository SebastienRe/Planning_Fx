package com.ceri.planningfx;

import com.ceri.planningfx.utilities.Mode;
import com.ceri.planningfx.utilities.Router;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class PlanningApplication extends Application {
    private static Stage mainStage;
    @Override
    public void start(Stage stage) throws IOException, InterruptedException {
        mainStage = stage;
        Router.changeScene("connexion.fxml");
        stage.setTitle("CERI-Planning");
        stage.getIcons().add(new Image("file:./resources/images/logo.png"));
        stage.show();

        Router.changeScene("connexion.fxml");
    }
    public static Stage getMainStage() {
        return mainStage;
    }

    public static void main(String[] args) {
        launch();
    }
}