package it.polimi.se2019.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class GUIApp extends Application {

    private static final String PATH = "utils/style/fxml/";

    private Stage stage;
    private GUIView view;

    @Override
    public void start(Stage window) {
        this.stage = window;
        this.stage.setTitle("Adrenaline - Sign up");
        this.view = new GUIView(Integer.parseInt(getParameters().getRaw().get(0)), this);
    }

    void showAlert(String text) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Adrenaline");
            alert.setHeaderText(text);
            alert.show();
        });
    }

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
            }
            try {
                this.stage.setScene(new Scene(loader.load()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            AbstractSceneController controller = loader.getController();
            controller.setView(this.view);
            this.stage.show();
            synchronized (this.view) {
                this.view.setActiveController(controller);
                this.view.notifyAll();
            }
        });
    }
}


