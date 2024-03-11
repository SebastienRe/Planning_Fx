package com.ceri.planningfx.utilities;

import com.ceri.planningfx.PlanningApplication;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

public class AccountService {
    static JSONObject connectedAccount = null;

    public static JSONObject getConnectedAccount() {
        return connectedAccount;
    }

    private static JSONArray getAccounts() {
        try {
            URL ressource = PlanningApplication.class.getResource
                    ("data/accounts/accounts.json");
            JSONObject json = (JSONObject) new JSONParser().parse(new FileReader(ressource.getPath()));
            return (JSONArray) json.get("accounts");
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean connexion(String username, String password) {
        for (Object account : getAccounts()) {
            JSONObject acc = (JSONObject) account;
            if (acc.get("username").equals(username) && acc.get("mdp").equals(password)) {
                Router.changeScene("main.fxml");
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
