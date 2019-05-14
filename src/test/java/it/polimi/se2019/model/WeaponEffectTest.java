package it.polimi.se2019.model;

import org.junit.Test;

import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertNull;

public class WeaponEffectTest {

    @Test
    public void getEffectNameTest() {
        WeaponEffect effect = Weapon.ZX_2.getAlternativeMode().get(0);
        assertEquals("modalità scanner", effect.getEffectName());
    }

    @Test
    public void getDescriptionTest() {
        WeaponEffect effect = Weapon.ZX_2.getAlternativeMode().get(0);
        assertEquals("Scegli fino a 3 bersagli che puoi vedere e dai 1 marchio a ciascuno",
                effect.getDescription());
    }

    @Test
    public void getTypeTest() {
        WeaponEffect effect = Weapon.ZX_2.getAlternativeMode().get(0);
        assertEquals(EffectType.MARK, effect.getType());
    }

    @Test
    public void getAmountTest() {
        WeaponEffect effect = Weapon.ZX_2.getAlternativeMode().get(0);
        List<String> amount = Arrays.asList("1");
        assertEquals(amount, effect.getAmount());
    }

    @Test
    public void getTargetTest() {
        WeaponEffect effect = Weapon.ZX_2.getAlternativeMode().get(0);
        EffectTarget target = effect.getTarget();
        assertNotNull(target);
        assertEquals(TargetType.OTHERS, target.getType());
    }

    @Test
    public void getEffectConstraintsTest() {
        WeaponEffect effect = Weapon.ZX_2.getAlternativeMode().get(0);
        assertEquals(Collections.emptySet(), effect.getEffectConstraints());

        effect = Weapon.ROCKET_LAUNCHER.getPrimaryEffect().get(1);
        assertNotNull(effect.getEffectConstraints());
    }

    @Test
    public void getCostTest() {
        WeaponEffect effect = Weapon.ROCKET_LAUNCHER.getSecondaryEffectTwo().get(0);
        Map<AmmoType, Integer> cost = new EnumMap<>(AmmoType.class);
        cost.put(AmmoType.BLUE, 0);
        cost.put(AmmoType.RED, 0);
        cost.put(AmmoType.YELLOW, 1);
        assertEquals(cost, effect.getCost());
    }
}