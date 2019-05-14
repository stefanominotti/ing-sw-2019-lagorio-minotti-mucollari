package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.TurnType;

public class StartTurnMessage extends Message {

    private TurnType turnType;
    private GameCharacter player;

    public StartTurnMessage(TurnType turnType, GameCharacter player) {
        setMessageType(this.getClass());
        this.turnType = turnType;
        this.player = player;
    }

    public TurnType getTurnType() {
        return this.turnType;
    }

    public GameCharacter getPlayer() {
        return this.player;
    }
}
