package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.Weapon;

import java.util.List;

public class WeaponPickupSelectionMessage extends Message implements SingleReceiverMessage {

    private GameCharacter character;
    private List<Weapon> weapons;

    public WeaponPickupSelectionMessage(GameCharacter character, List<Weapon> availableWeapons) {
        setMessageType(this.getClass());
        this.character = character;
        this.weapons = availableWeapons;
    }

    @Override
    public GameCharacter getCharacter() {
        return this.character;
    }

    public List<Weapon> getWeapons() {
        return this.weapons;
    }
}
