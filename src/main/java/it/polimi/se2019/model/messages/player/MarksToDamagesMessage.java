package it.polimi.se2019.model.messages.player;

import it.polimi.se2019.model.GameCharacter;

public class MarksToDamagesMessage extends PlayerMessage {

    private GameCharacter attacker;

    public MarksToDamagesMessage(GameCharacter player, GameCharacter attacker) {
        super(PlayerMessageType.MARKS_TO_DAMAGES, player);
        this.attacker = attacker;
    }

    public GameCharacter getAttacker() {
        return this.attacker;
    }
}
