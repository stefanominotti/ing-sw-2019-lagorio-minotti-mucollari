package it.polimi.se2019.model.messages.selections;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.model.messages.MessageType;
import it.polimi.se2019.model.messages.SingleReceiverMessage;

import java.util.List;

/**
 * Class for handling message selection list
 * @param <T> type objects of which fill the list
 */
public class SelectionListMessage<T> extends Message implements SingleReceiverMessage {

    private SelectionMessageType type;
    private GameCharacter character;
    private List<T> list;

    /**
     * Class constructor, it builds a selection list message which contains the choices of a player
     * @param type of the selection message
     * @param character which has done the choices
     * @param list of the choices
     */
    public SelectionListMessage(SelectionMessageType type, GameCharacter character, List<T> list) {
        setMessageType(MessageType.SELECTION_LIST_MESSAGE);
        this.type = type;
        this.character = character;
        this.list = list;
    }

    /**
     * Gets the type of the selection message
     * @return type of the message
     */
    public SelectionMessageType getType() {
        return this.type;
    }

    /**
     * Gets the character, who made the choices
     * @return character who made the choices
     */
    public GameCharacter getCharacter() {
        return this.character;
    }

    /**
     * Gets a list which the objects contained in the message selection list
     * @return the list of the objects contained in it
     */
    public List<T> getList() {
        return this.list;
    }
}
