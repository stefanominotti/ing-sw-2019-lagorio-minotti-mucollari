package it.polimi.se2019.model.messages.turn;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.TurnType;
import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.model.messages.MessageType;

import java.awt.*;

public class TurnMessage extends Message {

    private GameCharacter character;
    private TurnMessageType type;
    private TurnType turnType;

    public TurnMessage(TurnMessageType type, TurnType turnType, GameCharacter character) {
        setMessageType(MessageType.TURN_MESSAGE);
        this.turnType = turnType;
        this.character = character;
        this.type = type;
    }

    public TurnMessage(TurnMessageType type, GameCharacter character) {
        setMessageType(MessageType.TURN_MESSAGE);
        this.character = character;
        this.type = type;
    }

    public GameCharacter getCharacter() {
        return this.character;
    }

    public TurnMessageType getType() {
        return this.type;
    }

    public TurnType getTurnType() {
        return this.turnType;
    }
}
