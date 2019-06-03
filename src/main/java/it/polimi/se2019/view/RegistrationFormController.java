package it.polimi.se2019.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.rmi.RemoteException;


public class RegistrationFormController {

    private GUIView view;

    @FXML
    private TextField nickname;

    @FXML
    private Button submitNicknameButton;

    public RegistrationFormController () {
    }

/*
    public void displayNicknameError() {
        if(nickname.getText().isEmpty()) {
            //Alert
        }
        else if() {

        }
        else{

        }
    }*/

    void setView(GUIView view) {
        this.view = view;
    }

    public void forwardNickname() {
        try {
            this.view.handleNicknameInput(this.nickname.getText());
        } catch (RemoteException e) { }
    }

}