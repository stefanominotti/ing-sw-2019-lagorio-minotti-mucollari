package it.polimi.se2019.view;

import it.polimi.se2019.model.*;

import org.junit.Before;
import org.junit.Test;

import java.util.EnumMap;
import java.util.Map;

public class SquareViewTest {

    private SquareView square;

    @Before
    public void setUp() {
        Map<CardinalPoint, Boolean> nearbyAccessibility = new EnumMap<>(CardinalPoint.class);
        nearbyAccessibility.put(CardinalPoint.NORTH, false);
        nearbyAccessibility.put(CardinalPoint.EAST, false);
        nearbyAccessibility.put(CardinalPoint.WEST, false);
        nearbyAccessibility.put(CardinalPoint.SOUTH, true);
        this.square = new SquareView(0, 0, RoomColor.YELLOW, false, nearbyAccessibility);
        /* this.square.addStoreWeapon(Weapon.ZX_2);
        this.square.addStoreWeapon(Weapon.FLAMETHROWER);
        this.square.addStoreWeapon(Weapon.THOR); */
        Map<AmmoType, Integer> ammos = new EnumMap<>(AmmoType.class);
        ammos.put(AmmoType.YELLOW, 1);
        ammos.put(AmmoType.RED, 1);
        AmmoTile tile = new AmmoTile(true, ammos);
        this.square.setAvailableAmmoTile(tile);
        this.square.addActivePlayer(GameCharacter.BANSHEE);
        this.square.addActivePlayer(GameCharacter.DOZER);
        this.square.addActivePlayer(GameCharacter.D_STRUCT_OR);
        this.square.addActivePlayer(GameCharacter.VIOLETTA);
        this.square.addActivePlayer(GameCharacter.SPROG);
    }

    @Test
    public void printTest() {
        System.out.println(this.square);
    }
}
