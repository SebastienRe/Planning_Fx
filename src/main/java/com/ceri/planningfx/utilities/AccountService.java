package com.ceri.planningfx.utilities;

import com.ceri.planningfx.PlanningApplication;
import com.ceri.planningfx.metier.ParserIcs;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;

public class AccountService {
    static JSONObject connectedAccount = null;
    static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static JSONObject getConnectedAccount() {
        return connectedAccount;
    }

    public static void setAttributeToAccount(String key, Object value) {
        if (connectedAccount != null) {
            connectedAccount.put(key, value);
            serializeConnectedAccount();
        }
    }

    private static void refreshConnectedAccount() {
        if (connectedAccount != null) {
            connectedAccount = (JSONObject) getAccounts().stream()
                    .filter(account -> ((JSONObject) account).get("username").equals(connectedAccount.get("username")))
                    .findFirst().orElse(null);
        }
    }

    private static void serializeConnectedAccount() {
        JSONArray allAccounts = getAccounts();
        for (int i = 0; i < allAccounts.size(); i++) {
            JSONObject account = (JSONObject) allAccounts.get(i);
            if (account.get("username").equals(connectedAccount.get("username"))) {
                allAccounts.set(i, connectedAccount);
                break;
            }
        }

        JSONObject jsonFile = new JSONObject();
        jsonFile.put("accounts", allAccounts);

        try {
            URL resource = PlanningApplication.class.getResource("data/accounts/accounts.json");
            String filePath = Paths.get(resource.toURI()).toString();
            System.out.println("Writing to file: " + filePath);
            try (FileWriter writer = new FileWriter(filePath)) {
                gson.toJson(jsonFile, writer);
            }
            System.out.println("Successfully wrote to file");
        } catch (IOException | java.net.URISyntaxException e) {
            System.out.println("Failed to write to file");
            e.printStackTrace();
        }

        refreshConnectedAccount();
    }

    private static JSONArray getAccounts() {
        try {
            URL resource = PlanningApplication.class.getResource("data/accounts/accounts.json");
            String filePath = Paths.get(resource.toURI()).toString();
            JSONObject json = (JSONObject) new JSONParser().parse(new FileReader(filePath));
            return (JSONArray) json.get("accounts");
        } catch (IOException | ParseException | java.net.URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean connexion(String username, String password) {
        for (Object account : getAccounts()) {
            JSONObject acc = (JSONObject) account;
            if (acc.get("username").equals(username) && acc.get("mdp").equals(password)) {
                ParserIcs.foleder = "users";
                ParserIcs.file = acc.get("username") + ".ics";
                connectedAccount = acc;
                return true;
            }
        }
        return false;
    }

    public static void deconnexion() {
        connectedAccount = null;
        Router.changeScene("connexion.fxml");
    }
}
