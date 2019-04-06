package it.polimi.se2019.controller;

import it.polimi.se2019.model.GameCharacter;

public class ActionSelectedEvent {

    private GameCharacter player;
    private ActionType action;

    public ActionSelectedEvent(GameCharacter player, ActionType action) {
        this.player = player;
        this.action = action;
    }

    public GameCharacter getPlayer() {
        return null;
    }

    public ActionType getAction() {
        return null;
    }
}
