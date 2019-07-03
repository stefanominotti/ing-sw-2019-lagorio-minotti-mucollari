package it.polimi.se2019.model.playerassets.weapons;

import it.polimi.se2019.model.playerassets.AmmoType;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.EnumMap;
import java.util.Map;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.TestCase.assertEquals;

public class WeaponTest {

    @Test
    public void nameTest() {
        assertEquals("DISTRUTTORE", Weapon.LOCK_RIFLE.getName());
        assertEquals("FALCE PROTONICA", Weapon.ELECTROSCYTHE.getName());
        assertEquals("MITRAGLIATRICE", Weapon.MACHINE_GUN.getName());
        assertEquals("RAGGIO TRAENTE", Weapon.TRACTOR_BEAM.getName());
        assertEquals("TORPEDINE", Weapon.THOR.getName());
        assertEquals("CANNONE VORTEX", Weapon.VORTEX_CANNON.getName());
        assertEquals("VULCANIZZATORE", Weapon.FURNACE.getName());
        assertEquals("FUCILE AL PLASMA", Weapon.PLASMA_GUN.getName());
        assertEquals("RAZZO TERMICO", Weapon.HEATSEEKER.getName());
        assertEquals("FUCILE DI PRECISIONE", Weapon.WHISPER.getName());
        assertEquals("RAGGIO SOLARE", Weapon.HELLION.getName());
        assertEquals("LANCIAFIAMME", Weapon.FLAMETHROWER.getName());
        assertEquals("ZX-2", Weapon.ZX_2.getName());
        assertEquals("LANCIAGRANATE", Weapon.GRENADE_LAUNCHER.getName());
        assertEquals("FUCILE A POMPA", Weapon.SHOTGUN.getName());
        assertEquals("LANCIARAZZI", Weapon.ROCKET_LAUNCHER.getName());
        assertEquals("CYBERGUANTO", Weapon.POWER_GLOVE.getName());
        assertEquals("FUCILE LASER", Weapon.RAILGUN.getName());
        assertEquals("ONDA D'URTO", Weapon.SHOCKWAVE.getName());
        assertEquals("SPADA FOTONICA", Weapon.CYBERBLADE.getName());
        assertEquals("MARTELLO IONICO", Weapon.SLEDGEHAMMER.getName());
    }

    @Test
    public void colorTest() {
        TestCase.assertEquals(AmmoType.BLUE, Weapon.LOCK_RIFLE.getColor());
        assertEquals(AmmoType.BLUE, Weapon.ELECTROSCYTHE.getColor());
        assertEquals(AmmoType.BLUE, Weapon.MACHINE_GUN.getColor());
        assertEquals(AmmoType.BLUE, Weapon.TRACTOR_BEAM.getColor());
        assertEquals(AmmoType.BLUE, Weapon.THOR.getColor());
        assertEquals(AmmoType.RED, Weapon.VORTEX_CANNON.getColor());
        assertEquals(AmmoType.RED, Weapon.FURNACE.getColor());
        assertEquals(AmmoType.BLUE, Weapon.PLASMA_GUN.getColor());
        assertEquals(AmmoType.RED, Weapon.HEATSEEKER.getColor());
        assertEquals(AmmoType.BLUE, Weapon.WHISPER.getColor());
        assertEquals(AmmoType.RED, Weapon.HELLION.getColor());
        assertEquals(AmmoType.RED, Weapon.FLAMETHROWER.getColor());
        assertEquals(AmmoType.YELLOW, Weapon.ZX_2.getColor());
        assertEquals(AmmoType.RED, Weapon.GRENADE_LAUNCHER.getColor());
        assertEquals(AmmoType.YELLOW, Weapon.SHOTGUN.getColor());
        assertEquals(AmmoType.RED, Weapon.ROCKET_LAUNCHER.getColor());
        assertEquals(AmmoType.YELLOW, Weapon.POWER_GLOVE.getColor());
        assertEquals(AmmoType.YELLOW, Weapon.RAILGUN.getColor());
        assertEquals(AmmoType.YELLOW, Weapon.SHOCKWAVE.getColor());
        assertEquals(AmmoType.YELLOW, Weapon.CYBERBLADE.getColor());
        assertEquals(AmmoType.YELLOW, Weapon.SLEDGEHAMMER.getColor());
    }

    @Test
    public void buyCostTest() {

        Map<AmmoType, Integer> testingCost = new EnumMap<>(AmmoType.class);

        testingCost.put(AmmoType.BLUE, 1);
        testingCost.put(AmmoType.RED, 0);
        testingCost.put(AmmoType.YELLOW, 0);
        assertEquals(testingCost, Weapon.LOCK_RIFLE.getBuyCost());

        testingCost = new EnumMap<>(AmmoType.class);
        testingCost.put(AmmoType.BLUE, 0);
        testingCost.put(AmmoType.RED, 0);
        testingCost.put(AmmoType.YELLOW, 0);
        assertEquals(testingCost, Weapon.ELECTROSCYTHE.getBuyCost());

        testingCost = new EnumMap<>(AmmoType.class);
        testingCost.put(AmmoType.BLUE, 0);
        testingCost.put(AmmoType.RED, 1);
        testingCost.put(AmmoType.YELLOW, 0);
        assertEquals(testingCost, Weapon.MACHINE_GUN.getBuyCost());

        testingCost = new EnumMap<>(AmmoType.class);
        testingCost.put(AmmoType.BLUE, 0);
        testingCost.put(AmmoType.RED, 0);
        testingCost.put(AmmoType.YELLOW, 0);
        assertEquals(testingCost, Weapon.TRACTOR_BEAM .getBuyCost());

        testingCost = new EnumMap<>(AmmoType.class);
        testingCost.put(AmmoType.BLUE, 0);
        testingCost.put(AmmoType.RED, 1);
        testingCost.put(AmmoType.YELLOW, 0);
        assertEquals(testingCost, Weapon.THOR.getBuyCost());

        testingCost = new EnumMap<>(AmmoType.class);
        testingCost.put(AmmoType.BLUE, 1);
        testingCost.put(AmmoType.RED, 0);
        testingCost.put(AmmoType.YELLOW, 0);
        assertEquals(testingCost, Weapon.VORTEX_CANNON.getBuyCost());

        testingCost = new EnumMap<>(AmmoType.class);
        testingCost.put(AmmoType.BLUE, 1);
        testingCost.put(AmmoType.RED, 0);
        testingCost.put(AmmoType.YELLOW, 0);
        assertEquals(testingCost, Weapon.FURNACE.getBuyCost());

        testingCost = new EnumMap<>(AmmoType.class);
        testingCost.put(AmmoType.BLUE, 0);
        testingCost.put(AmmoType.RED, 0);
        testingCost.put(AmmoType.YELLOW, 1);
        assertEquals(testingCost, Weapon.PLASMA_GUN.getBuyCost());

        testingCost = new EnumMap<>(AmmoType.class);
        testingCost.put(AmmoType.BLUE, 0);
        testingCost.put(AmmoType.RED, 1);
        testingCost.put(AmmoType.YELLOW, 1);
        assertEquals(testingCost, Weapon.HEATSEEKER.getBuyCost());

        testingCost = new EnumMap<>(AmmoType.class);
        testingCost.put(AmmoType.BLUE, 1);
        testingCost.put(AmmoType.RED, 0);
        testingCost.put(AmmoType.YELLOW, 1);
        assertEquals(testingCost, Weapon.WHISPER.getBuyCost());

        testingCost = new EnumMap<>(AmmoType.class);
        testingCost.put(AmmoType.BLUE, 0);
        testingCost.put(AmmoType.RED, 0);
        testingCost.put(AmmoType.YELLOW, 1);
        assertEquals(testingCost, Weapon.HELLION.getBuyCost());

        testingCost = new EnumMap<>(AmmoType.class);
        testingCost.put(AmmoType.BLUE, 0);
        testingCost.put(AmmoType.RED, 0);
        testingCost.put(AmmoType.YELLOW, 0);
        assertEquals(testingCost, Weapon.FLAMETHROWER.getBuyCost());

        testingCost = new EnumMap<>(AmmoType.class);
        testingCost.put(AmmoType.BLUE, 0);
        testingCost.put(AmmoType.RED, 1);
        testingCost.put(AmmoType.YELLOW, 0);
        assertEquals(testingCost, Weapon.ZX_2.getBuyCost());

        testingCost = new EnumMap<>(AmmoType.class);
        testingCost.put(AmmoType.BLUE, 0);
        testingCost.put(AmmoType.RED, 0);
        testingCost.put(AmmoType.YELLOW, 0);
        assertEquals(testingCost, Weapon.GRENADE_LAUNCHER.getBuyCost());

        testingCost = new EnumMap<>(AmmoType.class);
        testingCost.put(AmmoType.BLUE, 0);
        testingCost.put(AmmoType.RED, 0);
        testingCost.put(AmmoType.YELLOW, 1);
        assertEquals(testingCost, Weapon.SHOTGUN.getBuyCost());

        testingCost = new EnumMap<>(AmmoType.class);
        testingCost.put(AmmoType.BLUE, 0);
        testingCost.put(AmmoType.RED, 1);
        testingCost.put(AmmoType.YELLOW, 0);
        assertEquals(testingCost, Weapon.ROCKET_LAUNCHER.getBuyCost());

        testingCost = new EnumMap<>(AmmoType.class);
        testingCost.put(AmmoType.BLUE, 1);
        testingCost.put(AmmoType.RED, 0);
        testingCost.put(AmmoType.YELLOW, 0);
        assertEquals(testingCost, Weapon.POWER_GLOVE.getBuyCost());

        testingCost = new EnumMap<>(AmmoType.class);
        testingCost.put(AmmoType.BLUE, 1);
        testingCost.put(AmmoType.RED, 0);
        testingCost.put(AmmoType.YELLOW, 1);
        assertEquals(testingCost, Weapon.RAILGUN.getBuyCost());

        testingCost = new EnumMap<>(AmmoType.class);
        testingCost.put(AmmoType.BLUE, 0);
        testingCost.put(AmmoType.RED, 0);
        testingCost.put(AmmoType.YELLOW, 0);
        assertEquals(testingCost, Weapon.SHOCKWAVE.getBuyCost());

        testingCost = new EnumMap<>(AmmoType.class);
        testingCost.put(AmmoType.BLUE, 0);
        testingCost.put(AmmoType.RED, 1);
        testingCost.put(AmmoType.YELLOW, 0);
        assertEquals(testingCost, Weapon.CYBERBLADE.getBuyCost());

        testingCost = new EnumMap<>(AmmoType.class);
        testingCost.put(AmmoType.BLUE, 0);
        testingCost.put(AmmoType.RED, 0);
        testingCost.put(AmmoType.YELLOW, 0);
        assertEquals(testingCost, Weapon.SLEDGEHAMMER.getBuyCost());
    }

    @Test
    public void primaryEffectTest() {
        for(Weapon testingWeapon : Weapon.values()) {
            assertNotNull(testingWeapon.getPrimaryEffect());
        }
    }

    @Test
    public void secondaryUseTest() {

        assertEquals(0, Weapon.LOCK_RIFLE.getAlternativeMode().size());
        assertNotNull(Weapon.LOCK_RIFLE.getSecondaryEffectOne());
        assertEquals(0, Weapon.LOCK_RIFLE.getSecondaryEffectTwo().size());

    }

}