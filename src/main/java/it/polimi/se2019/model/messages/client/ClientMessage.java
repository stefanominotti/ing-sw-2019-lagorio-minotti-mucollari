package it.polimi.se2019.model.messages.client;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.model.messages.MessageType;
import it.polimi.se2019.model.messages.SingleReceiverMessage;
import it.polimi.se2019.model.messages.player.PlayerMessageType;

public class ClientMessage extends Message implements SingleReceiverMessage {

    private GameCharacter character;
    private ClientMessageType type;

    public ClientMessage(ClientMessageType type, GameCharacter character) {
        setMessageType(MessageType.CLIENT_MESSAGE);
        this.type = type;
        this.character = character;
    }

    public ClientMessage(ClientMessageType type) {
        setMessageType(MessageType.CLIENT_MESSAGE);
        this.type = type;
        this.character = null;
    }

    public GameCharacter getCharacter() {
        return this.character;
    }

    public ClientMessageType getType() {
        return this.type;
    }
}
