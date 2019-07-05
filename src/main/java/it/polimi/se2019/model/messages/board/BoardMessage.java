package it.polimi.se2019.model.messages.board;

import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.model.messages.MessageType;

/**
 * Class for handling board message
 * @author stefanominotti
 */
public class BoardMessage extends Message {

    private BoardMessageType type;

    /**
     * Class constructor, it builds a board message
     * @param type of the board message
     */
    public BoardMessage(BoardMessageType type) {
        setMessageType(MessageType.BOARD_MESSAGE);
        this.type = type;
    }

    /**
     * Gets the board message type
     * @return type of the board message
     */
    public BoardMessageType getType() {
        return this.type;
    }
}