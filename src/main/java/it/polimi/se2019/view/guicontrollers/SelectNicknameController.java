package it.polimi.se2019.view.guicontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * Class for handling select nickname controller
 * @author antoniolagorio
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