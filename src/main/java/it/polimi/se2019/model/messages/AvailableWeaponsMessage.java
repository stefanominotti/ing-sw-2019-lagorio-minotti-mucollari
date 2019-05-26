package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.Weapon;
import it.polimi.se2019.model.WeaponEffect;

import java.util.List;
import java.util.Map;

public class AvailableWeaponsMessage extends Message implements SingleReceiverMessage {

    private final GameCharacter character;
    private final Map<Weapon, List<List<WeaponEffect>>> weapons;
    public AvailableWeaponsMessage(GameCharacter character, Map<Weapon, List<List<WeaponEffect>>> availableWeapons) {
        this.character = character;
        this.weapons = availableWeapons;
    }

    public GameCharacter getCharacter() {
        return this.character;
    }

    public Map<Weapon, List<List<WeaponEffect>>> getWeapons() {
        return this.weapons;
    }
}
