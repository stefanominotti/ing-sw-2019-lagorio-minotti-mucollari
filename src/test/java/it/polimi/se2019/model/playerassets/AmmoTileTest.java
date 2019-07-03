package it.polimi.se2019.model.playerassets;

import it.polimi.se2019.model.playerassets.AmmoTile;
import it.polimi.se2019.model.playerassets.AmmoType;
import org.junit.Test;

import java.util.EnumMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class AmmoTileTest {

    @Test
    public void getAmmosTest() {
        Map<AmmoType, Integer> ammos = new EnumMap<>(AmmoType.class);
        ammos.put(AmmoType.RED, 1);
        ammos.put(AmmoType.BLUE, 2);
        ammos.put(AmmoType.YELLOW, 0);
        AmmoTile tile = new AmmoTile(false, ammos);
        Map<AmmoType, Integer> tileAmmos = tile.getAmmos();
        assertEquals(ammos, tileAmmos);
    }

    @Test
    public void hasPowerupTest() {
        Map<AmmoType, Integer> ammos = new EnumMap<>(AmmoType.class);
        ammos.put(AmmoType.RED, 1);
        ammos.put(AmmoType.BLUE, 2);
        ammos.put(AmmoType.YELLOW, 0);
        AmmoTile tile = new AmmoTile(false, ammos);
        assertFalse(tile.hasPowerup());
        ammos.put(AmmoType.RED, 1);
        ammos.put(AmmoType.BLUE, 1);
        ammos.put(AmmoType.YELLOW, 0);
        tile = new AmmoTile(true, ammos);
        assertTrue(tile.hasPowerup());
    }
}
