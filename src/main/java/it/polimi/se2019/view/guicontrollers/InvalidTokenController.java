package it.polimi.se2019.view.guicontrollers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Class for handling invalid token controller
 */
public class InvalidTokenController extends AbstractSceneController {

    @FXML
    Button removeButton;
    @FXML
    Label removeSuccessLabel;
    @FXML
    Label removeFailedLabel;

    /**
     * Tries to remove an invalid user token saved, on click event
     * After timer, it closes the scene and exit from the app
     */
    public void removeToken() {
        Platform.runLater(() -> {
            try {
                getView().removeToken();
                removeButton.setVisible(false);
                removeSuccessLabel.setText("Token was successfully removed");
            } catch (IOException e) {
                removeButton.setVisible(false);
                removeFailedLabel.setText("Can't remove file, it doesn't exist");
            }
        });
        (new Timer()).schedule(new TimerTask() {
            @Override
            public void run() {
               System.exit(0);
            }
        }, 3*1000L);
    }
}


