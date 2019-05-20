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

    boolean canApply(List<WeaponEffect> effect) {

        return true;
    }

    List<Square> getAvailableSquares(WeaponEffect effect, Player activePlayer) {
        return new ArrayList<>();
    }

    List<List<Player>> getAvailableTargets(WeaponEffect effect, Player activePlayer) {
        List<List<Player>> availableTargets = new ArrayList<>();
        return availableTargets;
    }

    void endAttack() {}

    List<List<WeaponEffect>> manageEffectChoise (WeaponCard weapon, String effectChosen) {
        List<List<WeaponEffect>> availableEffects = new ArrayList<>();
        availableEffects.add(weapon.getWeaponType().getPrimaryEffect());
        effectChosen = effectChosen.toLowerCase();
        switch (effectChosen) {
            case ("p"): {
                if (!mainEffectApplied) {
                    if (canApply(weapon.getWeaponType().getPrimaryEffect())) {
                        //metodi per performing action
                    } else {
                        //messaggio azione non eseguibile
                    }
                } else {
                    //effetto già usato o usata alternative mode
                }
                return availableEffects;
            }

            case ("a"): {
                if (!mainEffectApplied && weapon.getWeaponType().getAlternativeMode() != null) {
                    if (canApply(weapon.getWeaponType().getAlternativeMode())) {
                        //metodi per performing action
                        this.mainEffectApplied = true;
                        availableEffects.remove(weapon.getWeaponType().getAlternativeMode());
                        availableEffects.remove(weapon.getWeaponType().getPrimaryEffect());
                    } else {
                        //messaggio azione non eseguibile
                    }
                }
                return availableEffects;
            }
            case ("1"): {
                if (mainEffectApplied && weapon.getWeaponType().getSecondaryEffectOne() != null
                        && !this.secondaryEffectOneApplied) {
                    if (canApply(weapon.getWeaponType().getSecondaryEffectOne())) {
                        //metodi per performing action
                        this.secondaryEffectOneApplied = true;
                        availableEffects.remove(weapon.getWeaponType().getSecondaryEffectOne());
                    } else {
                        //messaggio azione non eseguibile
                    }
                }
                return availableEffects;
            }

            case ("2"): {
                if (mainEffectApplied && weapon.getWeaponType().getSecondaryEffectTwo() != null
                        && !this.secondaryEffectTwoApplied) {
                    if (canApply(weapon.getWeaponType().getSecondaryEffectOne())) {
                        //metodi per performing action
                        this.secondaryEffectTwoApplied = true;
                        availableEffects.remove(weapon.getWeaponType().getSecondaryEffectTwo());
                    } else {
                        //messaggio azione non eseguibile
                    }
                }
                return availableEffects;
            }

            default: {
                //scelta non valida
                return availableEffects;
            }
        }
    }


}
