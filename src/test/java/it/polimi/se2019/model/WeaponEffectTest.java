package it.polimi.se2019.model;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

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
    public void getConstraintsTest() {
        WeaponEffect effect = Weapon.ZX_2.getAlternativeMode().get(0);
        assertNull(effect.getConstraints());


        System.out.println(Weapon.LOCK_RIFLE.getPrimaryEffect().get(0).getDescription());
    }
}
