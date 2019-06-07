package it.polimi.se2019.model.messages.selections;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.model.messages.MessageType;
import it.polimi.se2019.model.messages.SingleReceiverMessage;

import java.util.List;

public class SelectionListMessage<T> extends Message implements SingleReceiverMessage {

    private SelectionMessageType type;
    private GameCharacter character;
    private List<T> list;

    public SelectionListMessage(SelectionMessageType type, GameCharacter character, List<T> list) {
        setMessageType(MessageType.SELECTION_LIST_MESSAGE);
        this.type = type;
        this.character = character;
        this.list = list;
    }

    public SelectionMessageType getType() {
        return this.type;
    }

    public GameCharacter getCharacter() {
        return this.character;
    }

    public List<T> getList() {
        return this.list;
    }
}
