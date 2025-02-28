package it.polimi.se2019.view.guicontrollers;

import it.polimi.se2019.model.GameCharacter;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

import java.util.List;

/**
 * Class for handling select arena controller+
 * @author antoniolagorio
 */
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
    private RadioButton violet;

    /**
     * Forwards a character choice
     */
    public void forwardCharacter() {
        String value;
        try {
            value = ((RadioButton) this.characterChoiceGroup.getSelectedToggle()).getId();
        } catch (NullPointerException e) {
            return;
        }
        getView().handleCharacterInput(GameCharacter.valueOf(value.toUpperCase()));
    }

    /**
     * Enables available character to choice
     * @param characters List of game characters that can be chosen
     */
    public void enableCharacters(List<GameCharacter> characters) {
        if(!characters.contains(GameCharacter.BANSHEE)) {
            this.banshee.setDisable(true);
        }
        if(!characters.contains(GameCharacter.D_STRUCT_OR)) {
            this.d_struct_or.setDisable(true);
        }
        if(!characters.contains(GameCharacter.VIOLET)) {
            this.violet.setDisable(true);
        }
        if(!characters.contains(GameCharacter.SPROG)) {
            this.sprog.setDisable(true);
        }
        if(!characters.contains(GameCharacter.DOZER)) {
            this.dozer.setDisable(true);
        }
    }
}