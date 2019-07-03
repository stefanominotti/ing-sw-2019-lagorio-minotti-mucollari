package it.polimi.se2019.model.playerassets.weapon;

import it.polimi.se2019.model.playerassets.weapon.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class EffectTargetTest {

    @Test
    public void constructorTest() {
        EffectTarget target = new EffectTarget(TargetType.OTHERS, Arrays.asList("1", "2"),
                TargetPositionType.EVERYWHERE, new ArrayList<>(), new ArrayList<>(), new HashSet<>());
        assertEquals(TargetType.OTHERS, target.getType());
        assertEquals(TargetPositionType.EVERYWHERE, target.getPositionType());
        assertEquals(Arrays.asList("1", "2"), target.getAmount());
    }

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
    public void getPositionTypeTest() {
        assertEquals(TargetPositionType.EVERYWHERE,
                Weapon.LOCK_RIFLE.getPrimaryEffect().get(0).getTarget().getPositionType());
    }

    @Test
    public void getPositionConstraintsTest() {
        List<String> distances = new ArrayList<>();
        List<PositionConstraint> weaponPositionConstraints =
                Weapon.LOCK_RIFLE.getPrimaryEffect().get(0).getTarget().getPositionConstraints();
        assertNotNull(weaponPositionConstraints);
        assertEquals(1, weaponPositionConstraints.size());
        assertEquals(PositionConstraintType.VISIBLE, weaponPositionConstraints.get(0).getType());
        assertEquals(distances, weaponPositionConstraints.get(0).getDistanceValues());
        assertEquals(TargetType.SELF, weaponPositionConstraints.get(0).getTarget());
    }

    @Test
    public void getAfterPositionConstraints() {
        assertEquals(0,
                Weapon.LOCK_RIFLE.getPrimaryEffect().get(0).getTarget().getAfterPositionConstraints().size());
    }

    @Test
    public void getTargetConstraintsTest() {
        assertEquals(1, Weapon.LOCK_RIFLE.getPrimaryEffect().get(1).getTarget().getTargetConstraints().size());
        assertEquals(TargetConstraint.ONLYHITBYMAIN,
                Weapon.LOCK_RIFLE.getPrimaryEffect().get(1).getTarget().getTargetConstraints().toArray()[0]);
    }
}