package it.polimi.se2019.controller;

import it.polimi.se2019.model.Player;

public class ActionSelectedEvent {

    private Player player;
    private ActionType action;

    public ActionSelectedEvent(Player player, ActionType action) {
        this.player = player;
        this.action = action;
    }

    public Player getPlayer() {}

    public ActionType getAction() {}
}
