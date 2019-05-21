package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.Weapon;
import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.model.messages.SingleReceiverMessage;

import java.util.List;

public class RequireWeaponLoadMessage extends Message implements SingleReceiverMessage {

    private GameCharacter character;
    private List<Weapon> weapons;

    public RequireWeaponLoadMessage(GameCharacter character, List<Weapon> unloadedWeapons) {
        setMessageType(this.getClass());
        this.character = character;
        this.weapons = unloadedWeapons;
    }

    @Override
    public GameCharacter getCharacter() {
        return this.character;
    }

    public List<Weapon> getWeapons() {
        return this.weapons;
    }

}
