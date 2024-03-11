package com.ceri.planningfx.controller;

import com.ceri.planningfx.PlanningApplication;
import com.ceri.planningfx.utilities.AccountService;
import com.ceri.planningfx.utilities.Router;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import javafx.scene.input.KeyCode;
import org.json.simple.JSONArray;

import org.json.simple.JSONObject;


import org.json.simple.parser.*;

public class ConnexionController {
    @FXML
    public TextField username;
    @FXML
    public PasswordField password;
    @FXML
    public Button loginButton;

    @FXML
    private Label infoLabel;

    public void initialize() {
        this.clearLabel();
        this.init_enter_listener();
    }

    public void init_enter_listener() {
        password.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    onConnectionButtonPressed();
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void clearLabel() {
        infoLabel.setText("");
    }

    @FXML
    public void onConnectionButtonPressed() throws IOException, ParseException {
        System.out.println("Connexion en cours...");
        //verification des champs
        if (username.getText().isEmpty() || password.getText().isEmpty()) {
            System.out.println("champs vides");
            username.setText("");
            password.setText("");
            infoLabel.setText("Veuillez remplir tous les champs");

        } else {
            this.clearLabel();

            if (AccountService.connexion(username.getText(), password.getText())) {
                Router.changeScene("main.fxml");
            } else {
                infoLabel.setText("Connexion échouée");
            }
        }
    }
}
