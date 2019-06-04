package it.polimi.se2019.view;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.rmi.RemoteException;


public class SelectNicknameController extends AbstractSceneController {

    @FXML
    private TextField nickname;

    public void forwardNickname() {
        try {
            getView().handleNicknameInput(this.nickname.getText());
        } catch (RemoteException e) { }
    }

}