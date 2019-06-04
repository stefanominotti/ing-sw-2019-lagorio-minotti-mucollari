package it.polimi.se2019.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.se2019.view.CLIView;
import it.polimi.se2019.view.GUIApp;
import javafx.application.Application;

import java.io.FileReader;
import java.io.IOException;

public class Client {

    private static final String CONFIG_PATH = System.getProperty("user.home");

    public static void main(String[] args) {

        int connection;
        int UI;

        try {
            FileReader configReader = new FileReader(CONFIG_PATH + "/" + "client_settings.json");
            JsonParser parser = new JsonParser();
            connection = ((JsonObject)parser.parse(configReader)).get("connection").getAsInt();
            configReader = new FileReader(CONFIG_PATH + "/" + "client_settings.json");
            UI = ((JsonObject)parser.parse(configReader)).get("UI").getAsInt();
        } catch (IOException e) {
            connection = 0;
            UI = 0;
        }

        if (connection != 0 && connection != 1) {
            connection = 0;
        }

        if (UI != 0 && UI != 1) {
            UI = 0;
        }

        if (UI == 0) {
            new CLIView(connection);
        } else {
            String[] arguments = { String.valueOf(connection) };
            Application.launch(GUIApp.class, arguments);
        }
    }
}
