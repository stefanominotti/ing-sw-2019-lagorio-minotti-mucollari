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

    public List<String> getAmount() { }

    public EffectTarget getTarget() { }

    public Set<EffectConstraint> getConstraints() {}

    public Map<AmmoType, Integer> getCost() {}
}
