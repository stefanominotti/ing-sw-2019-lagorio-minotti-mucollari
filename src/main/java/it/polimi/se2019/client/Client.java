package it.polimi.se2019.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.se2019.view.CLIView;
import it.polimi.se2019.view.GUIApp;
import javafx.application.Application;

import java.io.FileReader;

public class Client {

    private static final String CONFIG_PATH = System.getProperty("user.home");
    private static final String CLIENT_SETTINGS = "/client_settings.json";
    private static final int DEFAULT_UI = 0;
    private static final int DEFAULT_CONNECTION = 0;

    public static void main(String[] args) {

        int connection;
        int ui;
        String ip = null;
        int port = 0;
        FileReader configReader;

        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject jsonElement = null;
        try {
            configReader = new FileReader(CONFIG_PATH + CLIENT_SETTINGS);
            jsonElement = (JsonObject) parser.parse(configReader);
        } catch (Exception e) {
            System.out.println("Invalid settings file");
            System.exit(0);
        }

        try {
            ip = gson.fromJson(jsonElement.get("serverIP"), String.class);
            port = gson.fromJson(jsonElement.get("port"), Integer.class);
        } catch (Exception e) {
            System.out.println("Can't read IP or Port");
            System.exit(0);
        }
        try {
            connection = gson.fromJson(jsonElement.get("port"), Integer.class);
        } catch (Exception e) {
            connection = DEFAULT_CONNECTION;
            System.out.println("Invalid connection type, connection set to default " + DEFAULT_CONNECTION +
                    " (Socket)");
        }
        try {
            ui = gson.fromJson(jsonElement.get("UI"), Integer.class);
        } catch (Exception e) {
            ui = DEFAULT_UI;
            System.out.println("Invalid UI, UI set to default " + DEFAULT_UI + " (CLI)");
        }
        if (ui != DEFAULT_UI && ui != DEFAULT_UI + 1) {
            ui = DEFAULT_UI;
        }
        if (connection != DEFAULT_CONNECTION && connection != DEFAULT_CONNECTION + 1) {
            connection = DEFAULT_CONNECTION;
        }
        if (ui == 0) {
            new CLIView(connection, ip, port);
        } else {
            String[] arguments = {String.valueOf(connection), ip, String.valueOf(port)};
            Application.launch(GUIApp.class, arguments);
        }
    }
}
