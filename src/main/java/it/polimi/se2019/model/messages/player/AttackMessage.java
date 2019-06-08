package it.polimi.se2019.model.messages.player;

import it.polimi.se2019.model.EffectType;
import it.polimi.se2019.model.GameCharacter;

public class AttackMessage extends PlayerMessage {

    private GameCharacter attacker;
    private int amount;
    private EffectType attackType;

    public AttackMessage(GameCharacter character, GameCharacter attacker, int amount, EffectType attackType) {
        super(PlayerMessageType.ATTACK, character);
        this.attacker = attacker;
        this.amount = amount;
        this.attackType = attackType;
    }

    public GameCharacter getAttacker() {
        return this.attacker;
    }

    public int getAmount() {
        return this.amount;
    }

    public EffectType getAttackType() {
        return this.attackType;
    }
}
