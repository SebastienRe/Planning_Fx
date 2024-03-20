package com.ceri.planningfx.utilities;

import com.ceri.planningfx.controller.PopupController;
import javafx.fxml.FXMLLoader;
import com.ceri.planningfx.PlanningApplication;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.image.Image;

import java.io.IOException;


public class Router {

    private static int width = 950;
    private static int height = 400;
    public static void changeScene(String sceneName) {


        try {
            PlanningApplication.getMainStage();
            FXMLLoader fxmlLoader = new FXMLLoader(PlanningApplication.class.getResource(sceneName));
            Scene scene = new Scene(fxmlLoader.load(), width, Screen.getPrimary().getVisualBounds().getHeight());
            PlanningApplication.getMainStage().setScene(scene);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String displayPopUpAndWait(String text, Mode mode) {
        try {
            // Créer une nouvelle fenêtre
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL); // Bloquer l'accès à la fenêtre principale
            popupStage.setResizable(false);
            popupStage.getIcons().add(new Image("file:./resources/images/logo.png"));

            // Charger le fichier FXML
            FXMLLoader loader = new FXMLLoader(PlanningApplication.class.getResource("popup.fxml"));
            Parent root = loader.load();

            // Créer la scène
            Scene scene = new Scene(root);

            // Appliquer la scène à la fenêtre
            popupStage.setScene(scene);

            // Récupérer le contrôleur du fichier FXML
            PopupController controller = loader.getController();
            controller.text.setText(text);
            controller.stage = popupStage;
            controller.mode(mode);

            //Attendre la fermeture de la fenêtre
            popupStage.showAndWait();

            // Récupérer le résultat du popup
            return controller.getResult();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
