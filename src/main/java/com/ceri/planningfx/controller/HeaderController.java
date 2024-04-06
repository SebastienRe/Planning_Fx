package com.ceri.planningfx.controller;

import com.ceri.planningfx.PlanningApplication;
import com.ceri.planningfx.metier.ParserIcs;
import com.ceri.planningfx.utilities.AccountService;
import com.ceri.planningfx.utilities.HeaderManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class HeaderController {
    @FXML
    ToggleButton HambugerButton;

    private boolean darkMode = false;

    @FXML
    ImageView ImageTheme;

    @FXML
    ImageView ImageButton;

    @FXML
    private MenuButton menuButtonFormation;
    @FXML
    private MenuButton menuButtonSalle;
    @FXML
    private MenuButton menuButtonUtilisateur;

    public void initialize() {
        HambugerButton.setSelected(false);
        //creer un thread
        new Thread(this::waitMainBorderPane).start();
    }

    @FXML
    public void deconnexionButtonPressed() {
        HeaderManager.setEdtController(null);
        HeaderManager.setFiltresController(null);
        HeaderManager.setMainBorderPane(null);

        AccountService.deconnexion();
    }

    private void waitMainBorderPane() {
        while (HeaderManager.getMainBorderPane() == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        setActiveTheme();
        populateFormationMenu();
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
            AccountService.setAttributeToAccount("theme", "dark");
        else
            AccountService.setAttributeToAccount("theme", "light");
        this.setActiveTheme();
    }

    public void setActiveTheme() {
        if (AccountService.getConnectedAccount().get("theme").equals("dark")){
            this.darkMode = true;
            this.setDarkMode();
        }
        else if (AccountService.getConnectedAccount().get("theme").equals("light")){
            this.darkMode = false;
            this.setLightMode();
        }
        else {
            throw new IllegalArgumentException("Theme not found");
        }
    }

    private void setDarkMode() {
        System.out.println("dark mode");

        if (HeaderManager.getMainBorderPane().getStylesheets().contains(PlanningApplication.class.getResource("stylesheets/themeLight.css")))
            HeaderManager.getMainBorderPane().getStylesheets().remove(Objects.requireNonNull(PlanningApplication.class.getResource("stylesheets/themeLight.css")).toExternalForm());
        HeaderManager.getMainBorderPane().getStylesheets().add(Objects.requireNonNull(PlanningApplication.class.getResource("stylesheets/themeDark.css")).toExternalForm());

        ImageTheme.setImage(new Image(Objects.requireNonNull(PlanningApplication.class.getResource("images/mode-nuit.png")).toExternalForm()));
        ImageButton.setImage(new Image(Objects.requireNonNull(PlanningApplication.class.getResource("images/se-deconnecter-light.png")).toExternalForm()));
    }

    private void setLightMode() {
        System.out.println("light mode");

        HeaderManager.getMainBorderPane().getStylesheets().remove(Objects.requireNonNull(PlanningApplication.class.getResource("stylesheets/themeDark.css")).toExternalForm());
        HeaderManager.getMainBorderPane().getStylesheets().add(Objects.requireNonNull(PlanningApplication.class.getResource("stylesheets/themeLight.css")).toExternalForm());

        ImageTheme.setImage(new Image(Objects.requireNonNull(PlanningApplication.class.getResource("images/mode-jour.png")).toExternalForm()));
        ImageButton.setImage(new Image(Objects.requireNonNull(PlanningApplication.class.getResource("images/se-deconnecter-dark.png")).toExternalForm()));
    }

    private void populateFormationMenu() {
        // Obtenir le chemin absolu du dossier "resources"
        URL planningFolderURL = getClass().getResource("/com/ceri/planningfx/data/planning");
        assert planningFolderURL != null; // Vérifier que le dossier "planning" existe
        File planning = new File(planningFolderURL.getPath());

        // Vérifier si le dossier "formations" existe et est un répertoire
        if (planning.exists() && planning.isDirectory()) {
            File formation = new File(planning.getPath() + "/formations");
            File[] filesFormation = formation.listFiles();
            // Parcourir les fichiers dans le dossier "formations"
            if (filesFormation != null) {
                for (File file : filesFormation) {
                    if (file.isFile()) {
                        //enlever l'extension du fichier
                        MenuItem menuItem = new MenuItem(file.getName().substring(0, file.getName().lastIndexOf('.')));
                        menuItem.setOnAction(event -> {
                            ParserIcs.foleder = "formations";
                            ParserIcs.file = file.getName();
                            HeaderManager.getEdtController().refresh();
                        });
                        menuButtonFormation.getItems().add(menuItem);
                    }
                }
            }
            File salle = new File(planning.getPath() + "/salle");
            File[] filesSalle = salle.listFiles();
            // Parcourir les fichiers dans le dossier "salles"
            if (filesSalle != null) {
                for (File file : filesSalle) {
                    if (file.isFile()) {
                        //enlever l'extension du fichier
                        MenuItem menuItem = new MenuItem(file.getName().substring(0, file.getName().lastIndexOf('.')));
                        menuItem.setOnAction(event -> {
                            ParserIcs.foleder = "salle";
                            ParserIcs.file = file.getName();
                            HeaderManager.getEdtController().refresh();
                        });
                        menuButtonSalle.getItems().add(menuItem);
                    }
                }
            }
            File utilisateur = new File(planning.getPath() + "/users");
            File[] filesUtilisateur = utilisateur.listFiles();
            // Parcourir les fichiers dans le dossier "users"
            if (filesUtilisateur != null) {
                for (File file : filesUtilisateur) {
                    if (file.isFile()) {
                        //enlever l'extension du fichier
                        MenuItem menuItem = new MenuItem(file.getName().substring(0, file.getName().lastIndexOf('.')));
                        menuItem.setOnAction(event -> {
                            ParserIcs.foleder = "users";
                            ParserIcs.file = file.getName();
                            HeaderManager.getEdtController().refresh();
                        });
                        menuButtonUtilisateur.getItems().add(menuItem);
                    }
                }
            }
        }
    }

}
