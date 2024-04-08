package com.ceri.planningfx.controller;

import com.ceri.planningfx.metier.ParserIcs;
import com.ceri.planningfx.models.FiltresCollections;
import com.ceri.planningfx.utilities.AccountService;
import com.ceri.planningfx.utilities.HeaderManager;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;

import java.util.HashSet;
import java.util.Set;
public class FiltresController {

    @FXML
    private MenuButton matieresMenuButton;

    @FXML
    private MenuButton groupesMenuButton;

    @FXML
    private MenuButton typesCoursMenuButton;

    @FXML
    private MenuButton sallesMenuButton;

    @FXML
    private Button resetFiltersButton;
    private FiltresCollections filtresCollections;

    KeyCodeCombination ctrlZ = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN);

    public void initialize() {
        HeaderManager.setFiltresController(this);
        // Initialiser les ensembles de filtres
        filtresCollections = HeaderManager.getEdtController().filtresCollections;

        // Afficher les ensembles de filtres dans les MenuButton
        afficherFiltresDansMenuButton(matieresMenuButton, filtresCollections.listDesMatieres);
        afficherFiltresDansMenuButton(groupesMenuButton, filtresCollections.listDesGroupes);
        afficherFiltresDansMenuButton(typesCoursMenuButton, filtresCollections.typesDeCours);
        afficherFiltresDansMenuButton(sallesMenuButton, filtresCollections.listDesSalles);
        resetFilters();
        this.setActionsWithKeyboard();
    }

    private void afficherFiltresDansMenuButton(MenuButton menuButton, Set<String> filtres) {
        //refaire un parcrours et virer le max de doublon avec une analyse en string
        //car dans le fichier ics c'est mal ecrire
        //exemple
        //on peut trouver stat 6 et stat6 donc pour le set c'est pas la meme chose
        //donc on va faire une analyse en string pour virer les doublons
        Set<String> filtresNormalises = new HashSet<>();
        for (String filtre : filtres) {
            String filtreNormalise = normaliserFiltre(filtre);
            if (!filtresNormalises.contains(filtreNormalise)) {
                if (menuButton == sallesMenuButton) {
                    filtre = filtre.length() > 20 ? filtre.substring(0, 20) : filtre;
                }
                MenuItem menuItem = new MenuItem(filtre);
                menuItem.setOnAction(event -> {
                    // action au clique
                    HeaderManager.getEdtController().refreshWithFiltre(menuItem.getText());
                });
                menuButton.getItems().add(menuItem);
                filtresNormalises.add(filtreNormalise);
            }
        }
    }

    private String normaliserFiltre(String filtre) {
        // Normalisez la chaîne en minuscules et supprimez les espaces et les caractères non alphanumériques
        return filtre.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    //refresh les filtres
    public void refresh() {
        System.out.println("refresh filtre");
        filtresCollections = HeaderManager.getEdtController().filtresCollections;
        matieresMenuButton.getItems().clear();
        groupesMenuButton.getItems().clear();
        typesCoursMenuButton.getItems().clear();
        sallesMenuButton.getItems().clear();
        afficherFiltresDansMenuButton(matieresMenuButton, filtresCollections.listDesMatieres);
        afficherFiltresDansMenuButton(groupesMenuButton, filtresCollections.listDesGroupes);
        afficherFiltresDansMenuButton(typesCoursMenuButton, filtresCollections.typesDeCours);
        afficherFiltresDansMenuButton(sallesMenuButton, filtresCollections.listDesSalles);
    }
    private void resetFilters() {
        resetFiltersButton.setOnAction(event -> {
            HeaderManager.getEdtController().refresh();
        });
    }

    @FXML
    private VBox vbox;
    private void setActionsWithKeyboard()
    {

            // Récupérez la scène à partir de l'un des éléments graphiques
            Scene scene = vbox.getScene();
            if (scene != null) {
                System.out.println("scene non null dans filtre");
                // Ajoutez vos raccourcis clavier à la scène
                scene.getAccelerators().put(ctrlZ, this::refreshRacourci);

            }else {
                vbox.sceneProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        System.out.println("scene non null dans filtre");
                        // Ajoutez vos raccourcis clavier à la scène
                        newValue.getAccelerators().put(ctrlZ, this::refreshRacourci);
                    }
                });
            }

    }

    private void refreshRacourci()
    {
        HeaderManager.getEdtController().refresh();
    }
}
