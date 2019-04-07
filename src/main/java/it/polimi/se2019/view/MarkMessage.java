package it.polimi.se2019.view;

import it.polimi.se2019.model.GameCharacter;

public class MarkMessage {

    private GameCharacter attacker;
    private GameCharacter target;
    private int amount;

    public MarkMessage(GameCharacter attacker, GameCharacter target, int amount) {
        this.attacker = attacker;
        this.target = target;
        this.amount = amount;
    }

    public GameCharacter getAttacker() {
        return this.attacker;
    }

    public GameCharacter getTarget() {
        return this.target;
    }

    public int getAmount() {
        return this.amount;
    }
}
