package it.polimi.se2019.view.guicontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

/**
 * Class for handling select skulls controller
 * @author antoniolagorio
 */
public class SelectSkullsController extends AbstractSceneController {

    private static final int MIN_SKULLS = 3;
    private static final int MAX_SKULLS = 8;


    @FXML
    ComboBox skullsNumber;

    /**
     * Initializes the possible skulls number list
     */
    @FXML
    public void initialize() {
        for(int i=MIN_SKULLS; i<MAX_SKULLS+1; i++) {
           this.skullsNumber.getItems().add(i);
        }
    }

    /**
     * Forwards skulls number chosen
     */
    public void forwardSkullsNumber() {
        try {
            getView().handleSkullsInput((Integer) this.skullsNumber.getValue());
        } catch (NullPointerException e) {
            // Ignore
        }
    }
}
