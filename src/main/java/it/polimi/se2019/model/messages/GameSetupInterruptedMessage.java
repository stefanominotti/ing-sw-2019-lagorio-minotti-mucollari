package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.GameCharacter;

import java.util.List;

public class GameSetupInterruptedMessage extends Message {

    public GameSetupInterruptedMessage() {
        setMessageType(this.getClass());
    }
}
