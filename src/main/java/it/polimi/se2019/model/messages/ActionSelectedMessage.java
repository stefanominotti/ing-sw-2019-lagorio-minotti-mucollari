package it.polimi.se2019.model.messages;

import it.polimi.se2019.controller.ActionType;

public class ActionSelectedMessage extends Message {

    private ActionType action;

    public ActionSelectedMessage(ActionType action) {
        setMessageType(this.getClass());
        this.action = action;
    }

    public ActionType getAction() {
        return this.action;
    }
}
