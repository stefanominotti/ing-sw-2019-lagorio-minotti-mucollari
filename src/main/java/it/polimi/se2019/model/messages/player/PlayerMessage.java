package it.polimi.se2019.model.messages.player;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.model.messages.MessageType;
import it.polimi.se2019.model.messages.SingleReceiverMessage;

public class PlayerMessage extends Message implements SingleReceiverMessage {

    private GameCharacter character;
    private PlayerMessageType type;

    public PlayerMessage(PlayerMessageType type, GameCharacter character) {
        setMessageType(MessageType.PLAYER_MESSAGE);
        this.type = type;
        this.character = character;
    }

    @Override
    public GameCharacter getCharacter() {
        return this.character;
    }

    public PlayerMessageType getType() {
        return this.type;
    }
}
