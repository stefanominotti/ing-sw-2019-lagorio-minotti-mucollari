package it.polimi.se2019.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Class for handling GUI application
 */
public class GUIApp extends Application {

    private static final String PATH = "utils/style/fxml/";

    private Stage stage;
    private GUIView view;

    /**
     * Starts the application
     * @param window has to be shown
     */
    @Override
    public void start(Stage window) {
        this.stage = window;
        this.stage.setTitle("Adrenaline");
        this.view = new GUIView(Integer.parseInt(getParameters().getRaw().get(0)), getParameters().getRaw().get(1),
                Integer.parseInt(getParameters().getRaw().get(2)),this);
        this.stage.setOnCloseRequest(e -> System.exit(0));
    }

    /**
     * Shows an alert
     * @param text which has to be shown
     */
    void showAlert(String text) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Adrenaline");
            alert.setHeaderText(text);
            alert.show();
        });
    }

    /**
     * Sets the scene you want to show
     * @param scene you want to show
     */
     void setScene(SceneType scene) {
        Platform.runLater(() -> {
            FXMLLoader loader = null;
            switch (scene) {
                case SELECT_NICKNAME:
                    loader = new FXMLLoader(getClass().getClassLoader().getResource(PATH + "SelectNickname.fxml"));
                    break;
                case SELECT_CHARACTER:
                    loader = new FXMLLoader(getClass().getClassLoader().getResource(PATH + "SelectCharacter.fxml"));
                    break;
                case SELECT_SKULLS:
                    loader = new FXMLLoader(getClass().getClassLoader().getResource(PATH + "SelectSkulls.fxml"));
                    break;
                case SELECT_ARENA:
                    loader = new FXMLLoader(getClass().getClassLoader().getResource(PATH + "SelectArena.fxml"));
                    break;
                case LOBBY:
                    loader = new FXMLLoader(getClass().getClassLoader().getResource(PATH + "Lobby.fxml"));
                    break;
                case CONNECTION_ERROR:
                    loader = new FXMLLoader(getClass().getClassLoader().getResource(PATH + "ConnectionError.fxml"));
                    break;
                case RELOAD_GAME:
                    loader = new FXMLLoader(getClass().getClassLoader().getResource(PATH + "ReloadGame.fxml"));
                    break;
                case INVALID_TOKEN:
                    loader = new FXMLLoader(getClass().getClassLoader().getResource(PATH + "InvalidToken.fxml"));
                    break;
                case BOARD:
                    loader = new FXMLLoader(getClass().getClassLoader().getResource(PATH + "Board.fxml"));
                    break;
                case WEAPON_INFO:
                    loader = new FXMLLoader(getClass().getClassLoader().getResource(PATH + "CardDetail.fxml"));
                    break;
            }
            try {
                this.stage.setScene(new Scene(loader.load()));
            } catch (IOException e) {
                Thread.currentThread().interrupt();
            }
            AbstractSceneController controller = loader.getController();
            controller.setView(this.view);
            this.stage.centerOnScreen();
            this.stage.show();
            synchronized (this.view) {
                this.view.setActiveController(controller);
                this.view.notifyAll();
            }
        });
    }
}


