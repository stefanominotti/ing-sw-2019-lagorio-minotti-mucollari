package it.polimi.se2019.model;

import java.util.*;

public class WeaponEffect {

    private final String effectName;
    private final String description;
    private final Map<AmmoType, Integer> cost;
    private boolean effectRequired;
    private boolean effectCombo;
    private final List<String> effectDependency;
    private final EffectType type;
    private final List<String> amount;
    private final EffectTarget target;

    WeaponEffect(String effectName, String description, Map<AmmoType, Integer> cost, boolean effectRequired, boolean effectCombo,
                 List<String> effectDependency, EffectType type, List<String> amount, EffectTarget target) {

        this.effectName = effectName;
        this.description = description;
        this.cost = new EnumMap<>(AmmoType.class);
        this.cost.putAll(cost);
        this.effectRequired = effectRequired;
        this.effectCombo = effectCombo;
        this.effectDependency = effectDependency;
        this.type = type;
        this.amount = amount;
        this.target = target;
    }

    public String getEffectName(){ return this.effectName; }

    public String getDescription(){ return this.description; }

    public List<String> getEffectDependency() {
        if(effectDependency == null){
            return new ArrayList<>();
        }
        return new ArrayList<>(this.effectDependency);
    }

    public EffectType getType() {
        return this.type;
    }

    public List<String> getAmount() {
        return new ArrayList<>(this.amount);
    }

    public EffectTarget getTarget() {
        return this.target;
    }

    public boolean isRequired() { return this.effectRequired; }

    public boolean isCombo() { return this.effectCombo; }

    public Map<AmmoType, Integer> getCost() {
        Map<AmmoType, Integer> returnMap = new EnumMap<>(AmmoType.class);
        returnMap.putAll(this.cost);
        return returnMap;
    }
}
