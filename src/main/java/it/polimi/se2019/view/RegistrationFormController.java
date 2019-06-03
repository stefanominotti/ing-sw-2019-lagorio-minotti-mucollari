package it.polimi.se2019.view;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.rmi.RemoteException;


public class RegistrationFormController {

    private GUIView view;

    @FXML
    private TextField nickname;

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

    public void forwardNickname() {
        try {
            view.handleNicknameInput(nickname.getText());
        } catch (RemoteException e) { }

    }

}