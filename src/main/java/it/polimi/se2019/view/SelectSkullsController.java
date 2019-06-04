package it.polimi.se2019.view;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import java.rmi.RemoteException;

public class SelectSkullsController extends AbstractSceneController {

    @FXML
    ComboBox skullsNumber;

    @FXML
    public void initialize() {
        for(int i=3; i<9; i++) {
           this.skullsNumber.getItems().add(i);
        }
    }

    public void forwardSkullsNumber() throws RemoteException {
        try {
            getView().handleSkullsInput((Integer) this.skullsNumber.getValue());
        } catch (NullPointerException e) {
            // Ignore
        }
    }


}
