package it.polimi.se2019.view;

import it.polimi.se2019.model.GameCharacter;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

import java.util.List;


public class SelectCharacterController extends AbstractSceneController {

    @FXML
    private ToggleGroup characterChoiceGroup;
    @FXML
    private RadioButton banshee;
    @FXML
    private RadioButton d_struct_or;
    @FXML
    private RadioButton dozer;
    @FXML
    private RadioButton sprog;
    @FXML
    private RadioButton violetta;

    public void forwardCharacter() {
        String value;
        try {
            value = ((RadioButton) this.characterChoiceGroup.getSelectedToggle()).getId();
        } catch (NullPointerException e) {
            return;
        }
        getView().handleCharacterInput(GameCharacter.valueOf(value.toUpperCase()));
    }

    void enableCharacters(List<GameCharacter> characters) {
        if(!characters.contains(GameCharacter.BANSHEE)) {
            this.banshee.setDisable(true);
        }
        if(!characters.contains(GameCharacter.D_STRUCT_OR)) {
            this.d_struct_or.setDisable(true);
        }
        if(!characters.contains(GameCharacter.VIOLET)) {
            this.violetta.setDisable(true);
        }
        if(!characters.contains(GameCharacter.SPROG)) {
            this.sprog.setDisable(true);
        }
        if(!characters.contains(GameCharacter.DOZER)) {
            this.dozer.setDisable(true);
        }
    }
}