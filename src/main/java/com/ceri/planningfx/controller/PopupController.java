package com.ceri.planningfx.controller;

import com.ceri.planningfx.utilities.Mode;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class PopupController {
    @FXML
    public Label text;
    @FXML
    public HBox hBoxButtons;

    public Stage stage;
    public String answer = null;

    public String getResult() {
        return answer;
    }

    public void mode(Mode mode) {
        hBoxButtons.getChildren().clear();

        if (mode.equals(Mode.CHOIX)) {
            Button oui = new Button("Oui");
            oui.getStyleClass().add("button2");
            Button non = new Button("Non");
            non.getStyleClass().add("button2");
            hBoxButtons.getChildren().addAll(oui, non);

            //si le bouton oui est cliqué answer = oui et stage.close
            oui.setOnAction(event -> {
                answer = "oui";
                stage.close();
            });
            //si le bouton non est cliqué answer = non et stage.close
            non.setOnAction(event -> {
                answer = "non";
                stage.close();
            });

        } else if (mode.equals(Mode.OK)) {
            Button ok = new Button("Ok");
            ok.getStyleClass().add("button2");
            hBoxButtons.getChildren().add(ok);

            //si le bouton ok est cliqué answer = ok et stage.close
            ok.setOnAction(event -> {
                answer = "ok";
                stage.close();
            });
        }

    }
}
