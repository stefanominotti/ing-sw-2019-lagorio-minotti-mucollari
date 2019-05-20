package it.polimi.se2019.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EffectTargetTest {

    @Test
    public void getEffectTypeTest() {
        assertEquals(TargetType.OTHERS, Weapon.LOCK_RIFLE.getPrimaryEffect().get(0).getTarget().getType());
        assertEquals(TargetType.OTHERS, Weapon.LOCK_RIFLE.getPrimaryEffect().get(1).getTarget().getType());
    }


    @Test
    public void getAmountTest() {
        List<String> amount = new ArrayList<>();
        amount.add(0,"1");
        assertEquals(amount, Weapon.LOCK_RIFLE.getPrimaryEffect().get(0).getTarget().getAmount());
        amount.clear();
        amount.add(0,"1");
        assertEquals(amount, Weapon.LOCK_RIFLE.getPrimaryEffect().get(1).getTarget().getAmount());
    }

    @Test
    public void getPositionConstraintsTest() {
        List<String> distances = new ArrayList<>();
        List<PositionConstraint> weaponPositionConstraints = Weapon.LOCK_RIFLE.getPrimaryEffect().get(0).getTarget().getPositionConstraints();
        assertNotNull(weaponPositionConstraints);
        assertEquals(1, weaponPositionConstraints.size());
        assertEquals(PositionConstraintType.VISIBLE, weaponPositionConstraints.get(0).getType());
        assertEquals(distances, weaponPositionConstraints.get(0).getDistanceValues());
        assertEquals(TargetType.SELF, weaponPositionConstraints.get(0).getTarget());
    }

    @Test
    public void getAfterPositionConstraints() {
        List<PositionConstraint> positionConstraints = new ArrayList<>();
        assertEquals(positionConstraints, Weapon.LOCK_RIFLE.getPrimaryEffect().get(0).getTarget().getAfterPositionConstraints());
    }
}