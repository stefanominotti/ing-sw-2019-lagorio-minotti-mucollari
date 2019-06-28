package it.polimi.se2019.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class PositionConstraintTest {

    private PositionConstraint primaryPC;

    @Before
    public void setUp() {
        this.primaryPC = Weapon.LOCK_RIFLE.getPrimaryEffect().get(0).getTarget().getPositionConstraints().get(0);
    }

    @Test
    public void constructorTest() {
        PositionConstraint constraint = new PositionConstraint(PositionConstraintType.DISTANCE,
                Arrays.asList("1", "2"), TargetType.SELF);
        assertEquals(PositionConstraintType.DISTANCE, constraint.getType());
        assertEquals(Arrays.asList("1", "2"), constraint.getDistanceValues());
        assertEquals(TargetType.SELF, constraint.getTarget());
    }
    
    @Test
    public void getTypeTest() { assertEquals(PositionConstraintType.VISIBLE, primaryPC.getType()); }

    @Test
    public void getDistanceValuesTest(){
        ArrayList<String> distance = new ArrayList<>();
        assertEquals(distance, primaryPC.getDistanceValues());
    }

    @Test
    public void getTargetTest() { assertEquals(TargetType.SELF, primaryPC.getTarget()); }

}