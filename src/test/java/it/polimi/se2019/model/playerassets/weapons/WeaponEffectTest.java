package it.polimi.se2019.model.playerassets.weapons;

import it.polimi.se2019.model.playerassets.AmmoType;
import org.junit.Test;

import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.*;

public class WeaponEffectTest {

    @Test
    public void constructorTest() {
        WeaponEffect loaded = Weapon.LOCK_RIFLE.getPrimaryEffect().get(0);
        WeaponEffect effect = new WeaponEffect(loaded.getEffectName(), loaded.getDescription(), loaded.getCost(),
                loaded.isRequired(), loaded.isCombo(), loaded.getEffectDependency(), loaded.getType(),
                loaded.getAmount(), loaded.getTarget(), loaded.getRequiredDependency());
        assertEquals(loaded.getEffectName(), effect.getEffectName());
        assertEquals(loaded.isRequired(), effect.isRequired());
        assertEquals(loaded.getAmount(), effect.getAmount());
    }

    @Test
    public void getEffectNameTest() {
        WeaponEffect effect = Weapon.ZX_2.getAlternativeMode().get(0);
        assertEquals("modalita scanner", effect.getEffectName());
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
    public void getCostTest() {
        WeaponEffect effect = Weapon.ROCKET_LAUNCHER.getSecondaryEffectTwo().get(0);
        Map<AmmoType, Integer> cost = new EnumMap<>(AmmoType.class);
        cost.put(AmmoType.BLUE, 0);
        cost.put(AmmoType.RED, 0);
        cost.put(AmmoType.YELLOW, 1);
        assertEquals(cost, effect.getCost());
    }

    @Test
    public void isComboTest() {
        WeaponEffect effect = Weapon.ROCKET_LAUNCHER.getSecondaryEffectTwo().get(0);
        assertTrue(effect.isCombo());
        effect = Weapon.LOCK_RIFLE.getPrimaryEffect().get(0);
        assertFalse(effect.isCombo());
    }

    @Test
    public void getRequiredDependencyTest() {
        WeaponEffect effect = Weapon.ROCKET_LAUNCHER.getSecondaryEffectTwo().get(0);
        assertEquals(0, (int) effect.getRequiredDependency());
    }
}