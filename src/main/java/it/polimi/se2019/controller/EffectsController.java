package it.polimi.se2019.controller;

import it.polimi.se2019.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EffectsController {

    private Board board;
    private List<WeaponEffect> currentEffect;
    private Weapon weapon;
    private boolean mainEffectApplied;
    private boolean secondaryEffectOneApplied;
    private boolean secondaryEffectTwoApplied;
    private int effectIndex;
    private List<Player> hitByMain;
    private List<Player> hitBySecondary;
    private Player activeTarget;
    private List<List<WeaponEffect>> weaponEffects;
    private TurnController turnController;
    private List<WeaponEffect> effectsQueue;

    public EffectsController(Board board) {
        this.board = board;
        this.hitByMain = new ArrayList<>();
        this.hitBySecondary = new ArrayList<>();
        this.mainEffectApplied = false;
        this.secondaryEffectOneApplied = false;
        this.secondaryEffectTwoApplied = false;
        this.effectIndex = 0;
        this.weaponEffects = new ArrayList<>();
    }

    private void buildWeaponEffects(Weapon weapon) {
        this.weapon = weapon;
        this.weapon = weapon;
        if (weapon.getPrimaryEffect() != null) {
            weaponEffects.add(weapon.getPrimaryEffect());
        }
        if (weapon.getAlternativeMode() != null) {
            weaponEffects.add(weapon.getAlternativeMode());
        } else {
            if (weapon.getSecondaryEffectOne() != null) {
                weaponEffects.add(weapon.getSecondaryEffectOne());
                if (weapon.getSecondaryEffectTwo() != null) {
                    weaponEffects.add(weapon.getSecondaryEffectTwo());
                }
            }
        }
    }

    public List<List<WeaponEffect>> getWeaponEffects(Weapon weapon) {
        buildWeaponEffects(weapon);
        return weaponEffects;
    }

    public List<List<WeaponEffect>> getAvailableEffects() {
        List<List<WeaponEffect>> availableWeapons = new ArrayList<>();
        if(!mainEffectApplied) {
            availableWeapons.add(weapon.getPrimaryEffect());
            if(weapon.getAlternativeMode() != null && checkCost(weapon.getAlternativeMode())) {
                availableWeapons.add(weapon.getAlternativeMode());
            }
        }
        if(!secondaryEffectOneApplied && checkCost(weapon.getSecondaryEffectOne())
                && !weapon.getSecondaryEffectTwo().get(0).isCombo() && (
                weapon.getPrimaryEffect().get(0).getEffectDependency().contains("secondaryEffectOne") || mainEffectApplied)) {
            availableWeapons.add(weapon.getSecondaryEffectOne());
        }
        if(!secondaryEffectTwoApplied && checkCost(weapon.getSecondaryEffectTwo())
                && !weapon.getSecondaryEffectTwo().get(0).isCombo() && (
                !(weapon.getSecondaryEffectTwo().get(0).getEffectDependency().contains("secondaryEffectOne") &&
                        !secondaryEffectOneApplied) &&
                (weapon.getPrimaryEffect().get(0).getEffectDependency().contains("secondaryEffectTwo") || mainEffectApplied))) {
            availableWeapons.add(weapon.getSecondaryEffectTwo());
        }
        return availableWeapons;
    }

    public boolean checkCost(List<WeaponEffect> effect) {
        for(Map.Entry<AmmoType, Integer> cost :  effect.get(0).getCost().entrySet()) {
            Integer powerupAmmo = 0;
            for(Powerup powerup : turnController.getActivePlayer().getPowerups()) {
                if(powerup.getColor() == cost.getKey()){
                    powerupAmmo++;
                }
            }
            if(cost.getValue() > turnController.getActivePlayer().getAvailableAmmos().get(cost.getKey()) + powerupAmmo) {
                return false;
            }
        }
        return true;
    }

    public void efectSelected(String effectType) {
        List<WeaponEffect> effect = null;
        switch (effectType) {
            case "primary":
                effect = weapon.getPrimaryEffect();
                break;
            case "alternative":
                effect = weapon.getAlternativeMode();
                break;
            case "secondaryOne":
                effect = weapon.getSecondaryEffectOne();
                break;
            case "secondaryTwo":
                effect = weapon.getSecondaryEffectTwo();
                break;
        }
        for(WeaponEffect effectPart : effect) {
            if(!effectPart.equals(effect.get(0)) && !effectPart.getEffectDependency().isEmpty()) {
                //chiedi al giocatore se vuole attivare prima l'effetto secondario
                return;
            } else {
                this.effectsQueue.add(effectPart);
            }

        }
    }
}