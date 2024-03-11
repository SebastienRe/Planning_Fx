package com.ceri.planningfx.controller;

import com.ceri.planningfx.PlanningApplication;
import com.ceri.planningfx.utilities.HeaderManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class HeaderController {
    @FXML
    ToggleButton HambugerButton;

    private boolean darkMode = false;

    @FXML
    ImageView ImageTheme;

    @FXML
    ImageView ImageButton;

    public void initialize() {
        HambugerButton.setSelected(false);
        //creer un thread
        new Thread(this::waitMainBorderPane).start();
    }

    private void waitMainBorderPane() {
        while (HeaderManager.getMainBorderPane() == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.setLightMode();
    }

    @FXML
    public void toggleMenu() throws IOException {
        this.toggleFilters(HambugerButton.isSelected());
    }

    public void toggleFilters(boolean selected) throws IOException {
        if (selected) {
            // Charge le fichier FXML
            FXMLLoader loader = new FXMLLoader(PlanningApplication.class.getResource("filtres.fxml"));
            Node node = loader.load();

            //include le node dans le left de la borderpane du main.fxml
            HeaderManager.getMainBorderPane().setLeft(node);
        } else {
            HeaderManager.getMainBorderPane().setLeft(null);
        }
    }

    @FXML
    public void changeTheme() {
        this.darkMode = !this.darkMode;
        if (this.darkMode)
            this.setDarkMode();
        else
            this.setLightMode();
    }

    private void setDarkMode() {
        HeaderManager.getMainBorderPane().getStylesheets().remove(PlanningApplication.class.getResource("stylesheets/themeLight.css").toExternalForm());
        HeaderManager.getMainBorderPane().getStylesheets().add(PlanningApplication.class.getResource("stylesheets/themeDark.css").toExternalForm());

        ImageTheme.setImage(new Image(PlanningApplication.class.getResource("images/mode-nuit.png").toExternalForm()));
        ImageButton.setImage(new Image(PlanningApplication.class.getResource("images/se-deconnecter-light.png").toExternalForm()));
    }

    private void setLightMode() {
        HeaderManager.getMainBorderPane().getStylesheets().remove(PlanningApplication.class.getResource("stylesheets/themeDark.css").toExternalForm());
        HeaderManager.getMainBorderPane().getStylesheets().add(PlanningApplication.class.getResource("stylesheets/themeLight.css").toExternalForm());

        ImageTheme.setImage(new Image(PlanningApplication.class.getResource("images/mode-jour.png").toExternalForm()));
        ImageButton.setImage(new Image(PlanningApplication.class.getResource("images/se-deconnecter-dark.png").toExternalForm()));
    }
}
