package it.polimi.se2019.model.playerassets;

import it.polimi.se2019.model.playerassets.AmmoType;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class AmmoTypeTest {

    @Test
    public void getIdentifierTest() {
        assertEquals('R', AmmoType.RED.getIdentifier());
        assertEquals('B', AmmoType.BLUE.getIdentifier());
        assertEquals('Y', AmmoType.YELLOW.getIdentifier());
    }
}
