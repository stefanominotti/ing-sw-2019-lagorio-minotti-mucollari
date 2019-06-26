package it.polimi.se2019.model;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class GameCharacterTest {

    @Test
    public void getIdentifierTest() {
        assertEquals("\u03B1", GameCharacter.D_STRUCT_OR.getIdentifier());
        assertEquals("\u03B2", GameCharacter.BANSHEE.getIdentifier());
        assertEquals("\u03B3", GameCharacter.DOZER.getIdentifier());
        assertEquals("\u03B4", GameCharacter.VIOLET.getIdentifier());
        assertEquals("\u03B5", GameCharacter.SPROG.getIdentifier());
    }
}
