package it.polimi.se2019.model;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class EffectTarget {

    private final TargetType type;
    private final List<String> amount;
    private final TargetPositionType positionType;
    private final List<PositionConstraint> positionConstraints;
    private final Set<TargetConstraint> targetConstraints;

    EffectTarget(TargetType type, List<String> amount, TargetPositionType positionType,
                        List<PositionConstraint> positionConstraints, Set<TargetConstraint> targetConstraints) {
        this.type = type;
        this.amount = amount;
        this.positionType = positionType;
        this.positionConstraints = positionConstraints;
        this.targetConstraints = EnumSet.copyOf(targetConstraints);
    }

    public TargetType getType() {
        return this.type;
    }

    public TargetPositionType getPositionType() {
        return this.positionType;
    }

    public List<String> getAmount() {}

    public List<PositionConstraint> getPositionConstraints() {}

    public Set<TargetConstraint> getTargetConstraints() {}
}
