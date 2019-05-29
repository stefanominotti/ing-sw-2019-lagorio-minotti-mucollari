package it.polimi.se2019.model.messages.board;

import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.model.messages.MessageType;

public class BoardMessage extends Message {

    public BoardMessageType type;

    public BoardMessage(BoardMessageType type) {
        setMessageType(MessageType.BOARD_MESSAGE);
        this.type = type;
    }

    public BoardMessageType getType() {
        return this.type;
    }
}
