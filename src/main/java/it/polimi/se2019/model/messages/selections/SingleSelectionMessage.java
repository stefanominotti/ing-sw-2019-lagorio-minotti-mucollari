package it.polimi.se2019.model.messages.selections;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.model.messages.MessageType;
import it.polimi.se2019.model.messages.SingleReceiverMessage;

/**
 * Class for
 */
public class SingleSelectionMessage extends Message implements SingleReceiverMessage {

    private SelectionMessageType type;
    private GameCharacter character;
    private Object selection;

    public SingleSelectionMessage(SelectionMessageType type, GameCharacter character, Object selection) {
        setMessageType(MessageType.SINGLE_SELECTION_MESSAGE);
        this.type = type;
        this.character = character;
        this.selection = selection;
    }

    public SelectionMessageType getType() {
        return this.type;
    }

    public GameCharacter getCharacter() {
        return this.character;
    }

    public Object getSelection() {
        return this.selection;
    }
}
