package it.polimi.se2019.view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class InvalidTokenController extends AbstractSceneController {
    private static final String PATH = System.getProperty("user.home");
    private static final String FILE_NAME = "/AdrenalinaClient.token";

    @FXML
    Button removeButton;
    @FXML
    Label removeSuccessLabel;
    @FXML
    Label removeFailedLabel;

    public void removeToken() {
        Platform.runLater(() -> {
            if (deleteFile(PATH + FILE_NAME)) {
                removeButton.setVisible(false);
                removeSuccessLabel.setText("Token was successfully removed");
            }
            else{
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

    private boolean deleteFile(String filePath) {
        File file = new File(filePath);
        return (file.delete());
    }
}


