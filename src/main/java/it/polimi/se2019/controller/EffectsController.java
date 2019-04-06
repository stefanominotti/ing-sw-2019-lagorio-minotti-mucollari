package it.polimi.se2019.controller;

import it.polimi.se2019.model.*;

import java.util.ArrayList;
import java.util.List;

public class EffectsController {

    private Board board;
    private WeaponCard weapon;
    private Player player;
    private List<WeaponEffect> currentEffect;
    private boolean mainEffectApplied;
    private boolean secondaryEffectOneApplied;
    private boolean secondaryEffectTwoApplied;
    private int effectIndex;
    private List<Player> hitByMain;
    private List<Player> hitBySecondary;
    private Player activeTarget;

    public EffectsController(Board board) {
        this.board = board;
        this.hitByMain = new ArrayList<>();
        this.hitBySecondary = new ArrayList<>();
        this.mainEffectApplied = false;
        this.secondaryEffectOneApplied = false;
        this.secondaryEffectTwoApplied = false;
        this.effectIndex = 0;
    }

    EffectType getActiveEffectType() {
        return null;
    }

    Player getActiveTarget() {
        return null;
    }

    void selectEffect(EffectSelectedEvent event) {}

    boolean canApply(List <WeaponEffect> effect) {
        return true;
    }

    List<Square> getAvailableSquares(WeaponEffect effect, Player player) {
        return new ArrayList<>();
    }

    List<Player> getAvailableTargets(WeaponEffect effect, Player player) {
        return new ArrayList<>();
    }

    void applyEffect(TargetSelectedEvent event) {}

    void endAttack() {}
}
