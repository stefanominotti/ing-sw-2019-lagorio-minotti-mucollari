package it.polimi.se2019.controller;

import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.Weapon;
import it.polimi.se2019.model.WeaponCard;

public class RealoadEvent {
    private Player player;
    private WeaponCard weapon;

    public RealoadEvent(Player player, WeaponCard weapon) {
        this.player = player;
        this.weapon = weapon;
    }

    public Player getPlayer() {}

    public WeaponCard getWeapon() {}
}
