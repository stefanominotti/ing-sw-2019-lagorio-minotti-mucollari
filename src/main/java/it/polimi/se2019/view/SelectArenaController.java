package it.polimi.se2019.view;

import it.polimi.se2019.model.GameCharacter;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

import java.rmi.RemoteException;
import java.util.List;


public class SelectArenaController extends AbstractSceneController {

    @FXML
    private ToggleGroup arenaChoiceGroup;
    @FXML
    private RadioButton arena_1;
    @FXML
    private RadioButton arena_2;
    @FXML
    private RadioButton arena_3;
    @FXML
    private RadioButton arena_4;

    public void forwardArenaNumber() throws RemoteException {
        String value;
        try {
            value = ((RadioButton) this.arenaChoiceGroup.getSelectedToggle()).getId().split("_")[1];
        } catch (NullPointerException e) {
            return;
        }
        getView().handleArenaInput(value);
    }

}