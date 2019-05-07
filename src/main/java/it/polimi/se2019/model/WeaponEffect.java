package it.polimi.se2019.model;

import java.util.*;

public class WeaponEffect {

    private final String effectName;
    private final String effectDescription;
    private final EffectType type;
    private final List<String> amount;
    private final Set<EffectConstraint> constraints;
    private final Set<EffectConstraint> afterConstraints;
    private final EffectTarget target;
    private final Map<AmmoType, Integer> cost;

    WeaponEffect(String effectName, String effectDescription, EffectType type, List<String> amount,
                    Set<EffectConstraint> constraints, Set<EffectConstraint> afterConstraints,
                    EffectTarget target, Map<AmmoType, Integer> cost) {

        this.effectName = effectName;
        this.effectDescription = effectDescription;
        this.type = type;
        this.amount = amount;
        this.constraints = EnumSet.copyOf(constraints);
        this.afterConstraints = EnumSet.copyOf(afterConstraints);
        this.target = target;
        this.cost = new EnumMap<>(AmmoType.class);
        this.cost.putAll(cost);
    }

    public String getEffectName(){ return this.effectName; }

    public String getEffectDescription(){ return this.effectDescription; }

    public EffectType getType() {
        return this.type;
    }

    public List<String> getAmount() {
        return new ArrayList<>();
    }

    public EffectTarget getTarget() {
        return null;
    }

    public Set<EffectConstraint> getConstraints() {
        return EnumSet.copyOf(this.constraints);
    }

    public Set<EffectConstraint> getAfterConstraints() {
        return EnumSet.copyOf(this.afterConstraints);
    }

    public Map<AmmoType, Integer> getCost() {
        return new EnumMap<>(AmmoType.class);
    }
}
