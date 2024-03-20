package com.ceri.planningfx.utilities;

import com.ceri.planningfx.PlanningApplication;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PlanningService {
    public static List<String> getListPlanning(String folderName) {
        List<String> listPlanning = new ArrayList<>();
        return listPlanning;
        /*
        String resourcesPath = PlanningApplication.class.getClassLoader().getResource("").getPath();
        return listerFichiersICS(resourcesPath + "/data/planning/" + folderName);
        */
    }

    public static List<String> listerFichiersICS(String cheminDossier) {
        List<String> fichiersICS = new ArrayList<>();

        File dossier = new File(cheminDossier);
        File[] fichiers = dossier.listFiles();

        if (fichiers != null) {
            for (File fichier : fichiers) {
                if (fichier.isFile() && fichier.getName().toLowerCase().endsWith(".ics")) {
                    fichiersICS.add(fichier.getName());
                }
            }
        }

        return fichiersICS;
    }
}
