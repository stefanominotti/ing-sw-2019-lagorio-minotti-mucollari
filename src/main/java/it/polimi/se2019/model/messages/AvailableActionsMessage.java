package it.polimi.se2019.model.messages;

import it.polimi.se2019.controller.ActionType;
import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.Player;

import java.util.List;

public class AvailableActionsMessage extends Message implements SingleReceiverMessage {
    private GameCharacter character;
    private List<ActionType> actions;

    public AvailableActionsMessage(GameCharacter character, List<ActionType> availableActions) {
        setMessageType(this.getClass());
        this.character = character;
        this.actions = availableActions;
    }

    @Override
    public GameCharacter getCharacter() {
        return this.character;
    }

    public List<ActionType> getActions() {
        return this.actions;
    }
}
