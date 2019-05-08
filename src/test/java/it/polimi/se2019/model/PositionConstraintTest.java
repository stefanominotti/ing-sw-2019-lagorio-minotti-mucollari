package it.polimi.se2019.model;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class PositionConstraintTest {

    private PositionConstraint primaryPC = Weapon.LOCK_RIFLE.getPrimaryEffect().get(0).getTarget().getPositionConstraints().get(0);
    private PositionConstraint secondaryOnePC = Weapon.LOCK_RIFLE.getSecondaryEffectOne().get(0).getTarget().getPositionConstraints().get(0);
    
    @Test
    public void getTypeTest() { assertEquals(PositionConstraintType.VISIBLE, primaryPC.getType()); }

    @Test
    public void getDistanceValuesTest(){
        ArrayList<String> distance = new ArrayList<>();
        assertEquals(distance, primaryPC.getDistanceValues());
    }

    @Test
    public void getTargetTest() { assertEquals(TargetType.SELF, primaryPC.getTarget().getType()); }

}