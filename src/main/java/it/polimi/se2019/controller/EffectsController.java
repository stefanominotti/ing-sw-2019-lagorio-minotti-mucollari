package it.polimi.se2019.controller;

import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.Square;
import it.polimi.se2019.model.WeaponCard;
import it.polimi.se2019.model.WeaponEffect;

import java.util.ArrayList;
import java.util.List;

public class EffectsController {

    private WeaponCard weapon;
    private Player player;
    private List<WeaponEffect> currentEffect;
    private boolean mainEffectApplied;
    private boolean secondaryEffectOneApplied;
    private boolean secondaryEffectTwoApplied;
    private int effectIndex;
    private List<Player> hitByMain;
    private List<Player> hitBySecondary;

    public EffectsController() {
        this.hitByMain = new ArrayList<>();
        this.hitBySecondary = new ArrayList<>();
        this.mainEffectApplied = false;
        this.secondaryEffectOneApplied = false;
        this.secondaryEffectTwoApplied = false;
        this.effectIndex = 0;
    }

    public void selectEffect(EffectSelectedEvent event) {}

    public boolean canApply(List <WeaponEffect> effect) {}

    public List<Square> getAvailableSquares(WeaponEffect, Player player)

    public List<Player> getAvailableTargets(WeaponEffect, Player player) {}

    public void applyEffect(TargetSelectedEvent event) {}

    private void endAttack() {}
}
