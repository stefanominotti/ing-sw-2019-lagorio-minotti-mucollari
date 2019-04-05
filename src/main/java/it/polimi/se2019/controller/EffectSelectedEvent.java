package it.polimi.se2019.controller;

import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.WeaponEffect;

import java.util.List;

public class EffectSelectedEvent {

    private Player player;
    private List<WeaponEffect> effect;

    public EffectSelectedEvent(Player player, List<WeaponEffect> effect) {
        this.player = player;
        this.effect = effect;
    }

    public Player getPlayer() {}

    public List<WeaponEffect> getEffect() {}
}
