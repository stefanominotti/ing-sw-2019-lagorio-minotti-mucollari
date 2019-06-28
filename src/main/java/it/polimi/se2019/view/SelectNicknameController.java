package it.polimi.se2019.view;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * Class for handling select nickname controller
 */
public class SelectNicknameController extends AbstractSceneController {

    @FXML
    private TextField nickname;

    /**
     * Forwards nickname chosen
     */
    public void forwardNickname() {
        getView().handleNicknameInput(this.nickname.getText());
    }

}