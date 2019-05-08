package it.polimi.se2019.model;

import java.util.ArrayList;
import java.util.List;

public class PositionConstraint {

    private final PositionConstraintType type;
    private final List<String> distanceValues;
    private final EffectTarget target;

    PositionConstraint(PositionConstraintType type, List<String> distanceValues, EffectTarget target) {
        this.type = type;
        this.distanceValues = distanceValues;
        this.target = target;
    }

    public EffectTarget getTarget() {
        return this.target;
    }

    public List<String> getDistanceValues() {
        if (distanceValues == null){
            return new ArrayList<>();
        }
        return this.distanceValues;
    }

    public PositionConstraintType getType() {
        return this.type;
    }
}
