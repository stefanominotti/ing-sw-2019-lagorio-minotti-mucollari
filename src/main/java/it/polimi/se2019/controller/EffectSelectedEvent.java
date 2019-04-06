package it.polimi.se2019.controller;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.WeaponEffect;

import java.util.ArrayList;
import java.util.List;

public class EffectSelectedEvent {

    private GameCharacter player;
    private List<WeaponEffect> effect;

    public EffectSelectedEvent(GameCharacter player, List<WeaponEffect> effect) {
        this.player = player;
        this.effect = effect;
    }

    public GameCharacter getPlayer() {
        return null;
    }

    public List<WeaponEffect> getEffect() {
        return new ArrayList<>();
    }
}
