package it.polimi.se2019.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class CoordinatesTest {
    Coordinates coordinates;

    @Before
    public void setUp() {
        this.coordinates = new Coordinates(0,0);
    }

    @Test
    public void getXTest() {
        assertEquals(0, this.coordinates.getX());
    }

    @Test
    public void getYTest() {
        assertEquals(0, this.coordinates.getY());
    }
}