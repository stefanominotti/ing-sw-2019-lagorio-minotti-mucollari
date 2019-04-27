package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.GameCharacter;

public class ClientDisconnectedMessage extends Message {

    private final GameCharacter character;
    private boolean reconnectionAllowed;

    public ClientDisconnectedMessage(GameCharacter character) {
        setMessageType(this.getClass());
        this.character = character;
    }

    public ClientDisconnectedMessage(GameCharacter character, boolean reconnectionAllowed) {
        setMessageType(this.getClass());
        this.character = character;
        this.reconnectionAllowed = reconnectionAllowed;
    }

    public GameCharacter getCharacter() {
        return this.character;
    }

    public boolean isReconnectionAllowed() {
        return this.reconnectionAllowed;
    }
}
