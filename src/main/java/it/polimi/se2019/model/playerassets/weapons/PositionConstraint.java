package it.polimi.se2019.model.playerassets.weapons;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for handling position constraints of targets
 * @author stefanominotti
 * @author antoniolagorio
 */
public class PositionConstraint {

    private final PositionConstraintType type;
    private final List<String> distanceValues;
    private final TargetType target;

    /**
     * Class constructor, it builds a position constraint
     * @param type of the position constraint
     * @param distanceValues constraint distances list if the position constraint type is a "distance", else null
     * @param target type of targets which the position constraint is referred to
     */
    PositionConstraint(PositionConstraintType type, List<String> distanceValues, TargetType target) {
        this.type = type;
        this.distanceValues = distanceValues;
        this.target = target;
    }

    /**
     * Gets the target type which the position constraint is referred to
     * @return the target type
     */
    public TargetType getTarget() {
        return this.target;
    }

    /**
     * Gets the distances list of the position constraint
     * @return the distance list if the position constraint type is a "distance", else empty list
     */
    public List<String> getDistanceValues() {
        if (this.distanceValues == null){
            return new ArrayList<>();
        }
        return this.distanceValues;
    }

    /**
     * Gets the position constraint type
     * @return the position constraint type
     */
    public PositionConstraintType getType() {
        return this.type;
    }
}