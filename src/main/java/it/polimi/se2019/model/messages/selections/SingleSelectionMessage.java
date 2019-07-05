package it.polimi.se2019.model.messages.selections;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.model.messages.MessageType;
import it.polimi.se2019.model.messages.SingleReceiverMessage;

/**
 * Class for handling a single selection
 */
public class SingleSelectionMessage extends Message implements SingleReceiverMessage {

    private SelectionMessageType type;
    private GameCharacter character;
    private Object selection;

    /**
     * Class constructor, it builds a single selection message which contains the choices of a player or the possible
     * choices to be sent
     * @param type of the selection message
     * @param character which has done the choices
     * @param selection object containing the choices
     */
    public SingleSelectionMessage(SelectionMessageType type, GameCharacter character, Object selection) {
        setMessageType(MessageType.SINGLE_SELECTION_MESSAGE);
        this.type = type;
        this.character = character;
        this.selection = selection;
    }

    /**
     * Gets the type of the selection message
     * @return type of the message
     */
    public SelectionMessageType getType() {
        return this.type;
    }

    /**
     * Gets the character, who made the choices or the addressee of the message
     * @return character who made the choices
     */
    public GameCharacter getCharacter() {
        return this.character;
    }

    /**
     * Gets the object representing the selection
     * @return the object representing the selection
     */
    public Object getSelection() {
        return this.selection;
    }
}
