package it.polimi.se2019.controller;

import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.WeaponCard;

import java.util.List;

public class ShotEvent {
    private Player player;
    private WeaponCard weapon;

    public ShotEvent(Player player, List<Player> targets, WeaponCard weapon) {
        this.player = player;
        this.weapon = weapon;
    }

    public Player getPlayer() {}

    public WeaponCard getWeapon() {}
}
