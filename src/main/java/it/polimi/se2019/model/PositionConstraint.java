package it.polimi.se2019.model;

import java.util.ArrayList;
import java.util.List;

public class PositionConstraint {

    private final PositionConstraintType type;
    private final List<String> distanceValues;
    private final TargetType target;

    PositionConstraint(PositionConstraintType type, List<String> distanceValues, TargetType target) {
        this.type = type;
        this.distanceValues = distanceValues;
        this.target = target;
    }

    public TargetType getTarget() {
        return this.target;
    }

    public List<String> getDistanceValues() {
        if (this.distanceValues == null){
            return new ArrayList<>();
        }
        return this.distanceValues;
    }

    public PositionConstraintType getType() {
        return this.type;
    }
}