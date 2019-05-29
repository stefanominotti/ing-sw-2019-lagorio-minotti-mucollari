package it.polimi.se2019.model.messages.client;

import it.polimi.se2019.model.GameCharacter;

public class ClientDisconnectedMessage extends ClientMessage {

    private boolean reconnectionAllowed;

    public ClientDisconnectedMessage(GameCharacter character) {
        super(ClientMessageType.DISCONNECTED, character);
    }

    public ClientDisconnectedMessage(GameCharacter character, boolean reconnectionAllowed) {
        super(ClientMessageType.DISCONNECTED, character);
        this.reconnectionAllowed = reconnectionAllowed;
    }

    public boolean isReconnectionAllowed() {
        return this.reconnectionAllowed;
    }
}
