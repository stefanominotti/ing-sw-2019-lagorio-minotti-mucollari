package it.polimi.se2019.view;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class SelectNicknameController extends AbstractSceneController {

    @FXML
    private TextField nickname;

    public void forwardNickname() {
        getView().handleNicknameInput(this.nickname.getText());
    }

}