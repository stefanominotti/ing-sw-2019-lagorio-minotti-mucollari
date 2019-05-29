package it.polimi.se2019.model.messages.selections;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.model.messages.MessageType;

public class SelectionReceivedMessage extends Message {

    private SelectionMessageType type;
    private GameCharacter character;
    private Object selection;

    public SelectionReceivedMessage(SelectionMessageType type, GameCharacter character, Object selection) {
        setMessageType(MessageType.SELECTION_RECEIVED_MESSAGE);
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
