package it.polimi.se2019.model;

import java.util.*;

public class WeaponEffect {

    private final EffectType type;
    private final List<String> amount;
    private final Set<EffectConstraint> constraints;
    private final EffectTarget target;
    private final Map<AmmoType, Integer> cost;

    WeaponEffect(EffectType type, List<String> amount, Set<EffectConstraint> constraints, EffectTarget target,
                        Map<AmmoType, Integer> cost) {
        this.type = type;
        this.amount = amount;
        this.constraints = EnumSet.copyOf(constraints);
        this.target = target;
        this.cost = new EnumMap<>(AmmoType.class);
        this.cost.putAll(cost);
    }

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

    public Map<AmmoType, Integer> getCost() {
        return new EnumMap<>(AmmoType.class);
    }
}
