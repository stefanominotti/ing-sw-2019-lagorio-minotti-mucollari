package it.polimi.se2019.model;

import java.util.*;

public class WeaponEffect {

    private final String effectName;
    private final String description;
    private final EffectType type;
    private final List<String> amount;
    private final Set<EffectConstraint> effectConstraints;
    private final EffectTarget target;
    private final Map<AmmoType, Integer> cost;

    WeaponEffect(String effectName, String description, EffectType type, List<String> amount,
                    Set<EffectConstraint> effectConstraints, EffectTarget target, Map<AmmoType, Integer> cost) {

        this.effectName = effectName;
        this.description = description;
        this.type = type;
        this.amount = amount;
        this.effectConstraints = EnumSet.copyOf(effectConstraints);
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

    public Set<EffectConstraint> getEffectConstraints() {
        if(this.effectConstraints == null) {
            return Collections.emptySet();
        }
        return EnumSet.copyOf(this.effectConstraints);
    }

    public Map<AmmoType, Integer> getCost() {
        Map<AmmoType, Integer> returnMap = new EnumMap<>(AmmoType.class);
        returnMap.putAll(this.cost);
        return returnMap;
    }
}
