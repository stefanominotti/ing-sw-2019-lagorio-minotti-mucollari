package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.Coordinates;
import it.polimi.se2019.model.GameCharacter;

import java.util.List;

public class AvailableMoveActionMessage extends Message implements SingleReceiverMessage {

    private GameCharacter character;
    private List<Coordinates> movements;

    public AvailableMoveActionMessage(GameCharacter character, List<Coordinates> movements) {
        setMessageType(this.getClass());
        this.character = character;
        this.movements = movements;
    }

    @Override
    public GameCharacter getCharacter() {
        return this.character;
    }

    public List<Coordinates> getMovements() {
        return this.movements;
    }
}
