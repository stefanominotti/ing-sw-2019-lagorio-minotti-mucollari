package it.polimi.se2019.view;

import it.polimi.se2019.model.GameCharacter;

public class AddKillshotMessage {

    private GameCharacter player;
    private int amount;

    public AddKillshotMessage(GameCharacter player, int amount) {
        this.player = player;
        this.amount = amount;
    }

    public GameCharacter getPlayer() {
        return this.player;
    }

    public int getAmount() {
        return this.amount;
    }
}
