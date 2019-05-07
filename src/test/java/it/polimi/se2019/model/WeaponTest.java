package it.polimi.se2019.model;

import org.junit.Test;

import javax.security.sasl.SaslServer;
import java.util.EnumMap;
import java.util.Map;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

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
        assertEquals(AmmoType.BLUE, Weapon.LOCK_RIFLE.getColor());
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

        boolean caught;

        try {
            caught = false;
            Weapon.LOCK_RIFLE.getAlternativeMode();
        } catch (IllegalStateException e) {
            caught = true;
        }
        assertTrue(caught);
        assertNotNull(Weapon.LOCK_RIFLE.getSecondaryEffectOne());
        try {
            caught = false;
            Weapon.LOCK_RIFLE.getSecondaryEffectTwo();
        } catch (IllegalStateException e) {
            caught = true;
        }
        assertTrue(caught);


        assertNotNull(Weapon.ELECTROSCYTHE.getAlternativeMode());
        try {
            caught = false;
            Weapon.ELECTROSCYTHE.getSecondaryEffectOne();
        } catch (IllegalStateException e) {
            caught = true;
        }
        assertTrue(caught);
        try {
            caught = false;
            Weapon.ELECTROSCYTHE.getSecondaryEffectTwo();
        } catch (IllegalStateException e) {
            caught = true;
        }
        assertTrue(caught);


        try {
            caught = false;
            Weapon.MACHINE_GUN.getAlternativeMode();
        } catch (IllegalStateException e) {
            caught = true;
        }
        assertTrue(caught);
        assertNotNull(Weapon.MACHINE_GUN.getSecondaryEffectOne());
        assertNotNull(Weapon.MACHINE_GUN.getSecondaryEffectTwo());


        assertNotNull(Weapon.TRACTOR_BEAM.getAlternativeMode());
        try {
            caught = false;
            Weapon.TRACTOR_BEAM.getSecondaryEffectOne();
        } catch (IllegalStateException e) {
            caught = true;
        }
        assertTrue(caught);
        try {
            caught = false;
            Weapon.TRACTOR_BEAM.getSecondaryEffectTwo();
        } catch (IllegalStateException e) {
            caught = true;
        }
        assertTrue(caught);


        try {
            caught = false;
            Weapon.THOR.getAlternativeMode();
        } catch (IllegalStateException e) {
            caught = true;
        }
        assertTrue(caught);
        assertNotNull(Weapon.THOR.getSecondaryEffectOne());
        assertNotNull(Weapon.THOR.getSecondaryEffectTwo());


        try {
            caught = false;
            Weapon.VORTEX_CANNON.getAlternativeMode();
        } catch (IllegalStateException e) {
            caught = true;
        }
        assertTrue(caught);
        assertNotNull(Weapon.VORTEX_CANNON.getSecondaryEffectOne());
        try {
            caught = false;
            Weapon.VORTEX_CANNON.getSecondaryEffectTwo();
        } catch (IllegalStateException e) {
            caught = true;
        }
        assertTrue(caught);


        assertNotNull(Weapon.FURNACE.getAlternativeMode());
        try {
            caught = false;
            Weapon.FURNACE.getSecondaryEffectOne();
        } catch (IllegalStateException e) {
            caught = true;
        }
        assertTrue(caught);
        try {
            caught = false;
            Weapon.FURNACE.getSecondaryEffectTwo();
        } catch (IllegalStateException e) {
            caught = true;
        }
        assertTrue(caught);


        try {
            caught = false;
            Weapon.PLASMA_GUN.getAlternativeMode();
        } catch (IllegalStateException e) {
            caught = true;
        }
        assertTrue(caught);
        assertNotNull(Weapon.PLASMA_GUN.getSecondaryEffectOne());
        assertNotNull(Weapon.PLASMA_GUN.getSecondaryEffectTwo());


        try {
            caught = false;
            Weapon.HEATSEEKER.getAlternativeMode();
        } catch (IllegalStateException e) {
            caught = true;
        }
        assertTrue(caught);
        try {
            caught = false;
            Weapon.HEATSEEKER.getSecondaryEffectOne();
        } catch (IllegalStateException e) {
            caught = true;
        }
        assertTrue(caught);
        try {
            caught = false;
            Weapon.HEATSEEKER.getSecondaryEffectTwo();
        } catch (IllegalStateException e) {
            caught = true;
        }
        assertTrue(caught);


        try {
            caught = false;
            Weapon.WHISPER.getAlternativeMode();
        } catch (IllegalStateException e) {
            caught = true;
        }
        assertTrue(caught);
        try {
            caught = false;
            Weapon.WHISPER.getSecondaryEffectOne();
        } catch (IllegalStateException e) {
            caught = true;
        }
        assertTrue(caught);
        try {
            caught = false;
            Weapon.WHISPER.getSecondaryEffectTwo();
        } catch (IllegalStateException e) {
            caught = true;
        }
        assertTrue(caught);


        assertNotNull(Weapon.HELLION.getAlternativeMode());
        try {
            caught = false;
            Weapon.HELLION.getSecondaryEffectOne();
        } catch (IllegalStateException e) {
            caught = true;
        }
        assertTrue(caught);
        try {
            caught = false;
            Weapon.HELLION.getSecondaryEffectTwo();
        } catch (IllegalStateException e) {
            caught = true;
        }
        assertTrue(caught);


        assertNotNull(Weapon.FLAMETHROWER.getAlternativeMode());
        try {
            caught = false;
            Weapon.FLAMETHROWER.getSecondaryEffectOne();
        } catch (IllegalStateException e) {
            caught = true;
        }
        assertTrue(caught);
        try {
            caught = false;
            Weapon.FLAMETHROWER.getSecondaryEffectTwo();
        } catch (IllegalStateException e) {
            caught = true;
        }
        assertTrue(caught);


        assertNotNull(Weapon.ZX_2.getAlternativeMode());
        try {
            caught = false;
            Weapon.ZX_2.getSecondaryEffectOne();
        } catch (IllegalStateException e) {
            caught = true;
        }
        assertTrue(caught);
        try {
            caught = false;
            Weapon.ZX_2.getSecondaryEffectTwo();
        } catch (IllegalStateException e) {
            caught = true;
        }
        assertTrue(caught);


        try {
            caught = false;
            Weapon.GRENADE_LAUNCHER.getAlternativeMode();
        } catch (IllegalStateException e) {
            caught = true;
        }
        assertTrue(caught);
        assertNotNull(Weapon.GRENADE_LAUNCHER.getSecondaryEffectOne());
        try {
            caught = false;
            Weapon.GRENADE_LAUNCHER.getSecondaryEffectTwo();
        } catch (IllegalStateException e) {
            caught = true;
        }
        assertTrue(caught);
        assertNotNull(Weapon.SHOTGUN.getAlternativeMode());
        try {
            caught = false;
            Weapon.SHOTGUN.getSecondaryEffectOne();
        } catch (IllegalStateException e) {
            caught = true;
        }
        assertTrue(caught);
        try {
            caught = false;
            Weapon.SHOTGUN.getSecondaryEffectTwo();
        } catch (IllegalStateException e) {
            caught = true;
        }
        assertTrue(caught);


        try {
            caught = false;
            Weapon.ROCKET_LAUNCHER.getAlternativeMode();
        } catch (IllegalStateException e) {
            caught = true;
        }
        assertTrue(caught);
        assertNotNull(Weapon.ROCKET_LAUNCHER.getSecondaryEffectOne());
        assertNotNull(Weapon.ROCKET_LAUNCHER.getSecondaryEffectTwo());


        assertNotNull(Weapon.POWER_GLOVE.getAlternativeMode());
        try {
            caught = false;
            Weapon.POWER_GLOVE.getSecondaryEffectOne();
        } catch (IllegalStateException e) {
            caught = true;
        }
        assertTrue(caught);
        try {
            caught = false;
            Weapon.POWER_GLOVE.getSecondaryEffectTwo();
        } catch (IllegalStateException e) {
            caught = true;
        }
        assertTrue(caught);


        assertNotNull(Weapon.RAILGUN.getAlternativeMode());
        try {
            caught = false;
            Weapon.RAILGUN.getSecondaryEffectOne();
        } catch (IllegalStateException e) {
            caught = true;
        }
        assertTrue(caught);
        try {
            caught = false;
            Weapon.RAILGUN.getSecondaryEffectTwo();
        } catch (IllegalStateException e) {
            caught = true;
        }
        assertTrue(caught);


        assertNotNull(Weapon.SHOCKWAVE.getAlternativeMode());
        try {
            caught = false;
            Weapon.SHOCKWAVE.getSecondaryEffectOne();
        } catch (IllegalStateException e) {
            caught = true;
        }
        assertTrue(caught);
        try {
            caught = false;
            Weapon.SHOCKWAVE.getSecondaryEffectTwo();
        } catch (IllegalStateException e) {
            caught = true;
        }
        assertTrue(caught);


        try {
            caught = false;
            Weapon.CYBERBLADE.getAlternativeMode();
        } catch (IllegalStateException e) {
            caught = true;
        }
        assertTrue(caught);
        assertNotNull(Weapon.CYBERBLADE.getSecondaryEffectOne());
        assertNotNull(Weapon.CYBERBLADE.getSecondaryEffectTwo());


        assertNotNull(Weapon.SLEDGEHAMMER.getAlternativeMode());
        try {
            caught = false;
            Weapon.SLEDGEHAMMER.getSecondaryEffectOne();
        } catch (IllegalStateException e) {
            caught = true;
        }
        assertTrue(caught);
        try {
            caught = false;
            Weapon.SLEDGEHAMMER.getSecondaryEffectTwo();
        } catch (IllegalStateException e) {
            caught = true;
        }
        assertTrue(caught);
    }

}