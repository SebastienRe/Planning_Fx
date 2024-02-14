package com.ceri.planningfx;

import com.ceri.planningfx.metier.ParserIcs;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class PlanningApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(PlanningApplication.class
                .getResource("connexion.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 400);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
        ParserIcs.parse("lyesISC.ics");
    }

    public static void main(String[] args) {
        launch();
    }
}