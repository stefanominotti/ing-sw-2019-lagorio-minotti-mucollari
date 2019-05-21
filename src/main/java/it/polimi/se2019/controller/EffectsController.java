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
    private List<List<WeaponEffect>> availableEffects;

    public EffectsController(Board board) {
        this.board = board;
        this.hitByMain = new ArrayList<>();
        this.hitBySecondary = new ArrayList<>();
        this.mainEffectApplied = false;
        this.secondaryEffectOneApplied = false;
        this.secondaryEffectTwoApplied = false;
        this.effectIndex = 0;
        this.availableEffects = new ArrayList<>();
        buildAvailableEffects(availableEffects);
    }

    private void buildAvailableEffects(List<List<WeaponEffect>> availableEffects) {
        if (weapon.getWeaponType().getPrimaryEffect() != null) {
            availableEffects.add(weapon.getWeaponType().getPrimaryEffect());
        }
        if (weapon.getWeaponType().getAlternativeMode() != null) {
            availableEffects.add(weapon.getWeaponType().getAlternativeMode());
        } else {
            if (weapon.getWeaponType().getSecondaryEffectOne() != null) {
                availableEffects.add(weapon.getWeaponType().getSecondaryEffectOne());
                if (weapon.getWeaponType().getSecondaryEffectTwo() != null) {
                    availableEffects.add(weapon.getWeaponType().getSecondaryEffectTwo());
                }
            }
        }
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

    public List<List<WeaponEffect>> getAvailableEffects() {
        return availableEffects; //TO DO display effects: [p] primario - [a] alt - [1] sec1 - [2] sec 2 - [X] uscita
    }

    public void checkEffectChoise(String effectChosen) {
        effectChosen = effectChosen.toLowerCase();

        switch (effectChosen) {
            case ("p"): {
                if (!mainEffectApplied) {
                    if (canApply(weapon.getWeaponType().getPrimaryEffect())) {
                        // chiamata a metodo che esegue applicazione effetto
                    } else {
                        //messaggio effetto non applicabile
                    }
                } else {
                    //messaggio azione non eseguibile
                }

            }

            case ("a"): {
                if (!mainEffectApplied && weapon.getWeaponType().getAlternativeMode() != null) {
                    if (canApply(weapon.getWeaponType().getAlternativeMode())) {
                        //metodi per performing action
                        this.mainEffectApplied = true;
                        availableEffects.remove(weapon.getWeaponType().getAlternativeMode());
                        availableEffects.remove(weapon.getWeaponType().getPrimaryEffect());
                    } else {
                        //messaggio effetto non applicabile
                    }
                } else {
                    //messaggio azione non eseguibile
                }
            }
            case ("1"): {
                if (mainEffectApplied && weapon.getWeaponType().getSecondaryEffectOne() != null
                        && !this.secondaryEffectOneApplied && !checkPrimaryEffectDependency("secondaryEffectOne")) {
                    if (canApply(weapon.getWeaponType().getSecondaryEffectOne())) {
                        //metodi per performing action
                        this.secondaryEffectOneApplied = true;
                        availableEffects.remove(weapon.getWeaponType().getSecondaryEffectOne());
                    } else {
                        //messaggio azione non eseguibile
                    }
                }
            }

            case ("2"): {
                if (mainEffectApplied && weapon.getWeaponType().getSecondaryEffectTwo() != null
                        && !this.secondaryEffectTwoApplied && !checkPrimaryEffectDependency("secondaryEffectTwo")) {
                    if (canApply(weapon.getWeaponType().getSecondaryEffectOne())) {
                        //metodi per performing action
                        this.secondaryEffectTwoApplied = true;
                        availableEffects.remove(weapon.getWeaponType().getSecondaryEffectTwo());
                    } else {
                        //messaggio azione non eseguibile
                    }
                }
            }

            default: { //scelta non valida
            }
        }

    }


    boolean checkPrimaryEffectDependency(String effectDependency) {
        for (effectIndex = 0; effectIndex < weapon.getWeaponType().getPrimaryEffect().size(); effectIndex++) {
            if (weapon.getWeaponType().getPrimaryEffect().get(effectIndex).getEffectDependency()
                    .contains(effectDependency)){
                return true;
            }
        }
        return false;
    }

    List<Square> getAvailableSquares(WeaponEffect effect, Player activePlayer) {
        return new ArrayList<>();
    }

    List<List<Player>> getAvailableTargets(WeaponEffect effect, Player activePlayer) {
        List<List<Player>> availableTargets = new ArrayList<>();
        return availableTargets;
    }

    void endAttack() { }
}