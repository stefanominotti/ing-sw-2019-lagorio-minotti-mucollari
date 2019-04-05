package it.polimi.se2019.controller;

import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.Powerup;
import it.polimi.se2019.model.WeaponCard;

public class CardPressedEvent {

    private Player player;
    private WeaponCard weapon;
    private Powerup powerup;

    public CardPressedEvent(Player player, WeaponCard weapon, Powerup powerup) {
        this.player = player;
        this.weapon = weapon;
        this.powerup = powerup;
    }

    public Player getPlayer() {}

    public WeaponCard getWeapon() {}

    public Powerup getPowerup() {}
}
