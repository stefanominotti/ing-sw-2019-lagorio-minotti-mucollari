package it.polimi.se2019.model.messages.player;

import it.polimi.se2019.model.playerassets.weapons.EffectType;
import it.polimi.se2019.model.GameCharacter;

/**
 * Class for handling attack message
 * @author stefanominotti
 */
public class AttackMessage extends PlayerMessage {

    private GameCharacter attacker;
    private int amount;
    private EffectType attackType;

    /**
     * Class constructor, it builds a attack message
     * @param character target of the attack
     * @param attacker character who want to perform the attack
     * @param amount of the effect
     * @param attackType type of the effect which the attacker wants to use
     */
    public AttackMessage(GameCharacter character, GameCharacter attacker, int amount, EffectType attackType) {
        super(PlayerMessageType.ATTACK, character);
        this.attacker = attacker;
        this.amount = amount;
        this.attackType = attackType;
    }

    /**
     * Gets the attacker
     * @return the Game Character of the attacker
     */
    public GameCharacter getAttacker() {
        return this.attacker;
    }

    /**
     * Gets the amount of the effect
     * @return the amount of the effect
     */
    public int getAmount() {
        return this.amount;
    }

    /**
     * Gets the attack type
     * @return the type of the effect which the attacker wants to use
     */
    public EffectType getAttackType() {
        return this.attackType;
    }
}
