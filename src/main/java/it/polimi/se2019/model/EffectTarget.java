package it.polimi.se2019.model;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class EffectTarget {

    private final TargetType type;
    private final List<String> amount;
    private final TargetPositionType positionType;
    private final List<PositionConstraint> positionConstraints;
    private final List<PositionConstraint> afterPositionConstraints;

    private final Set<TargetConstraint> targetConstraints;

    EffectTarget(TargetType type, List<String> amount, TargetPositionType positionType,
                        List<PositionConstraint> positionConstraints, List<PositionConstraint> afterPositionConstraints,
                        Set<TargetConstraint> targetConstraints) {

        this.type = type;
        this.amount = amount;
        this.positionType = positionType;
        this.positionConstraints = positionConstraints;
        this.afterPositionConstraints = afterPositionConstraints;
        this.targetConstraints = EnumSet.copyOf(targetConstraints);
    }

    public TargetType getType() {
        return this.type;
    }

    public TargetPositionType getPositionType() {
        return this.positionType;
    }

    public List<String> getAmount() {
        return new ArrayList<>();
    }

    public List<PositionConstraint> getPositionConstraints() {
        return new ArrayList<>();
    }

    public List<PositionConstraint> getAfterPositionConstraints() { return new ArrayList<>(); }

    public Set<TargetConstraint> getTargetConstraints() {
        return EnumSet.copyOf(this.targetConstraints);
    }
}
