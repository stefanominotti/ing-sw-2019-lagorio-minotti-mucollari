package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.AmmoType;
import it.polimi.se2019.model.GameCharacter;

import java.util.Map;

public class RequireWeaponPaymentMessage extends Message implements SingleReceiverMessage {

    private GameCharacter character;
    private Map<AmmoType, Integer> buyCost;

    public RequireWeaponPaymentMessage(GameCharacter character, Map<AmmoType, Integer> buyCost) {
        setMessageType(this.getClass());
        this.character = character;
        this.buyCost = buyCost;
    }

    @Override
    public GameCharacter getCharacter() {
        return character;
    }

    public Map<AmmoType, Integer> getBuyCost() {
        return buyCost;
    }
}
