package it.polimi.se2019.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PowerupTest {

    Powerup powerup = new Powerup(PowerupType.TARGETING_SCOPE, AmmoType.YELLOW);

    @Test
    public void getTypeTest() {
       PowerupType type = PowerupType.TARGETING_SCOPE;
       assertEquals(type, powerup.getType());
    }

    @Test
    public void getColorTest() {
        AmmoType color = AmmoType.YELLOW;
        assertEquals(color, powerup.getColor());
    }
}