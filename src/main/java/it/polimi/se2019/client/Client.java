package it.polimi.se2019.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.se2019.view.CLIView;
import it.polimi.se2019.view.GUIApp;
import javafx.application.Application;

import java.io.FileReader;
import java.io.IOException;

public class Client {

    private static final String CONFIG_PATH = System.getProperty("user.home");
    private static final String CLIENT_SETTINGS = "/client_settings.json";
    private static final int DEFAULT_UI = 0;

    public static void main(String[] args) {

        int connection = 0;
        int UI;
        String ip = null;
        int port = 0;
        FileReader configReader = null;

        try {
            configReader = new FileReader(CONFIG_PATH + CLIENT_SETTINGS);
        } catch (Exception e) {
            System.out.println("Invalid config file");
            System.exit(0);
        }
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject jsonElement = (JsonObject) parser.parse(configReader);
        try {
            connection = gson.fromJson(jsonElement.get("port"), Integer.class);
            ip = gson.fromJson(jsonElement.get("serverIP"), String.class);
            port = gson.fromJson(jsonElement.get("port"), Integer.class);
        } catch (Exception e) {
            System.out.println("Invalid IP, Port or Connection");
            System.exit(0);
        }
        try {
            UI = gson.fromJson(jsonElement.get("UI"), Integer.class);
        } catch (Exception e) {
            UI = DEFAULT_UI;
            System.out.println("Invalid UI, UI set to default " + DEFAULT_UI + " (CLI)");

        }
        if (UI != 0 && UI != 1) {
            UI = 0;
        }
        if (UI == 0) {
            new CLIView(connection, ip, port);
        } else {
            String[] arguments = {String.valueOf(connection), ip, String.valueOf(port)};
            Application.launch(GUIApp.class, arguments);
        }
    }
}
