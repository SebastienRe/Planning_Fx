package com.ceri.planningfx.controller;

import com.ceri.planningfx.PlanningApplication;
import com.ceri.planningfx.utilities.HeaderManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;

import java.io.IOException;

public class HeaderController {
    @FXML
    ToggleButton HambugerButton;

    public void initialize() {
        HambugerButton.setSelected(false);
    }

    @FXML
    public void toggleMenu() throws IOException {
        this.toggleFilters(HambugerButton.isSelected());
    }

    public void toggleFilters(boolean selected) throws IOException {
        if (selected) {
            System.out.println("HambugerButton.isSelected()");
            // Charge le fichier FXML
            System.out.println(PlanningApplication.class.getResource("filtres.fxml"));
            FXMLLoader loader = new FXMLLoader(PlanningApplication.class.getResource("filtres.fxml"));
            Node node = loader.load();

            //include le node dans le left de la borderpane du main.fxml
            HeaderManager.getMainBorderPane().setLeft(node);
        } else {
            System.out.println("HambugerButton.isSelected() else");
            HeaderManager.getMainBorderPane().setLeft(null);
        }
    }
}
