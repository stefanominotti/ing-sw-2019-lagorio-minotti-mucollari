package it.polimi.se2019.model;

import java.util.*;

/**
 * Class for handling the effect target
 */
public class EffectTarget {

    private final TargetType type;
    private final List<String> amount;
    private final TargetPositionType positionType;
    private final List<PositionConstraint> positionConstraints;
    private final List<PositionConstraint> afterPositionConstraints;
    private final Set<TargetConstraint> targetConstraints;

    /**
     * Class constructor, it builds an effect target
     * @param type target type
     * @param amount of targets
     * @param positionType target position type
     * @param positionConstraints position contraints before performing the effect
     * @param afterPositionConstraints position contraints after performing the effect
     * @param targetConstraints target constraints
     */
    EffectTarget(TargetType type, List<String> amount, TargetPositionType positionType,
                        List<PositionConstraint> positionConstraints, List<PositionConstraint> afterPositionConstraints,
                        Set<TargetConstraint> targetConstraints) {

        this.type = type;
        this.amount = amount;
        this.positionType = positionType;
        this.positionConstraints = positionConstraints;
        this.afterPositionConstraints = afterPositionConstraints;
        this.targetConstraints = targetConstraints;
    }

    /**
     * Gets the targets type of the effect
     * @return the target type of the effect
     */
    public TargetType getType() {
        return this.type;
    }

    /**
     * Gets the position type of the targets
     * @return the position type of the targets
     */
    public TargetPositionType getPositionType() {
        return this.positionType;
    }

    /**
     * Gets the amount of the targets
     * @return List with the amount of the targets
     */
    public List<String> getAmount() {
        if(this.amount == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(this.amount);
    }

    /**
     * Gets the position constraints of the targets before performing the effect
     * @return List of position constraints of the targets
     */
    public List<PositionConstraint> getPositionConstraints() {
        if (this.positionConstraints == null){
            return new ArrayList<>();
        }
            return new ArrayList<>(this.positionConstraints);
        }

    /**
     * Gets the position constraints of the targets after performing the effect
     * @return List of position constraints of the targets
     */
    public List<PositionConstraint> getAfterPositionConstraints() {
        if (this.afterPositionConstraints == null){
            return new ArrayList<>();
        }
        return new ArrayList<>(this.afterPositionConstraints);
    }

    /**
     * Gets the targets constraints
     * @return Set of target constraints
     */
    public Set<TargetConstraint> getTargetConstraints() {
        if(this.targetConstraints == null) {
            return new HashSet<>();
        }
        return EnumSet.copyOf(this.targetConstraints);
    }
}