package it.polimi.se2019.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.rmi.RemoteException;


public class GuiAppController {

    private GUIView view;

    @FXML
    private TextField nickname;

    @FXML
    private Button submitNicknameButton;

    public GuiAppController() {
    }

    void setView(GUIView view) {
        this.view = view;
    }

    public void forwardNickname() {
        try {
            this.view.handleNicknameInput(this.nickname.getText());
        } catch (RemoteException e) { }
    }

}