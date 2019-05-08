package it.polimi.se2019.model;

import java.util.*;

public class WeaponEffect {

    private final String effectName;
    private final String description;
    private final EffectType type;
    private final List<String> amount;
    private final Set<EffectConstraint> constraints;
    private final EffectTarget target;
    private final Map<AmmoType, Integer> cost;

    WeaponEffect(String effectName, String description, EffectType type, List<String> amount,
                    Set<EffectConstraint> constraints, EffectTarget target, Map<AmmoType, Integer> cost) {

        this.effectName = effectName;
        this.description = description;
        this.type = type;
        this.amount = amount;
        this.constraints = EnumSet.copyOf(constraints);
        this.target = target;
        this.cost = new EnumMap<>(AmmoType.class);
        this.cost.putAll(cost);
    }

    public String getEffectName(){ return this.effectName; }

    public String getDescription(){ return this.description; }

    public EffectType getType() {
        return this.type;
    }

    public List<String> getAmount() {
        return new ArrayList<>(this.amount);
    }

    public EffectTarget getTarget() {
        return this.target;
    }

    public Set<EffectConstraint> getConstraints() {
        if(this.constraints == null) {
            return null;
        }
        return EnumSet.copyOf(this.constraints);
    }

    public Map<AmmoType, Integer> getCost() {
        Map<AmmoType, Integer> returnMap = new EnumMap<>(AmmoType.class);
        returnMap.putAll(this.cost);
        return returnMap;
    }
}
