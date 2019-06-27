package it.polimi.se2019.view;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;


public class SelectArenaController extends AbstractSceneController {

    @FXML
    private ToggleGroup arenaChoiceGroup;

    public void forwardArenaNumber() {
        String value;
        try {
            value = ((RadioButton) this.arenaChoiceGroup.getSelectedToggle()).getId().split("_")[1];
        } catch (NullPointerException e) {
            return;
        }
        getView().handleArenaInput(value);
    }

}