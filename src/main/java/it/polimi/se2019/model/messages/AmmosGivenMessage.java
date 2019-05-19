package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.AmmoType;
import it.polimi.se2019.model.Coordinates;
import it.polimi.se2019.model.GameCharacter;

import java.util.Map;

public class AmmosGivenMessage extends Message {

    private GameCharacter character;
    private Map<AmmoType, Integer> ammos;
    private Coordinates coordinates;

    public AmmosGivenMessage(GameCharacter character, Map<AmmoType, Integer> ammos, Coordinates coordinates) {
        setMessageType(this.getClass());
        this.ammos = ammos;
        this.character = character;
        this.coordinates = coordinates;
    }

    public GameCharacter getCharacter() {
        return this.character;
    }

    public Map<AmmoType, Integer> getAmmos() {
        return this.ammos;
    }

    public Coordinates getCoordinates() {
        return this.coordinates;
    }
}
