package it.polimi.se2019.controller;

import it.polimi.se2019.model.*;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static it.polimi.se2019.model.WeaponEffectOrderType.ALTERNATIVE;
import static it.polimi.se2019.model.WeaponEffectOrderType.PRIMARY;
import static it.polimi.se2019.model.WeaponEffectOrderType.SECONDARYONE;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;


public class EffectControllerTest {
    Weapon weapon;
    Board board;
    Player player;
    Player p1;
    Player p2;
    TurnController turnController;
    EffectsController controller;
    //for pack
    List<GameCharacter> characters;
    List<Coordinates> squares;
    List<RoomColor> rooms;
    List<CardinalPoint> cardinalPoints;
    Map<Coordinates, List<GameCharacter>> multipleSquares;

    @Before
    public void setUp() {
        this.characters = new ArrayList<>();
        this.squares = new ArrayList<>();
        this.rooms = new ArrayList<>();
        this.cardinalPoints = new ArrayList<>();
        this.multipleSquares = new LinkedHashMap<>();

        this.board = new Board();
        this.board.createArena("4");
        this.board.addPlayer(GameCharacter.DOZER, "a", "123");
        this.board.addPlayer(GameCharacter.VIOLET, "b", "234");
        this.board.addPlayer(GameCharacter.BANSHEE, "c", "345");
        this.player = this.board.getPlayers().get(0);
        this.p1 = this.board.getPlayers().get(1);
        this.p2 = this.board.getPlayers().get(2);
        this.board.movePlayer(this.player, this.board.getArena().getSquareByCoordinate(1, 0));
        this.board.movePlayer(p1, this.board.getArena().getSquareByCoordinate(2, 0));
        this.board.movePlayer(p2, this.board.getArena().getSquareByCoordinate(1, 1));
        this.player.addWeapon(new WeaponCard(Weapon.LOCK_RIFLE));
        this.player.addWeapon(new WeaponCard(Weapon.TRACTOR_BEAM));
        this.player.addWeapon(new WeaponCard(Weapon.CYBERBLADE));
        this.player.addWeapon(new WeaponCard(Weapon.ROCKET_LAUNCHER));
        this.player.addWeapon(new WeaponCard(Weapon.FURNACE));
        this.player.addWeapon(new WeaponCard(Weapon.VORTEX_CANNON));
        this.player.addWeapon(new WeaponCard(Weapon.RAILGUN));
        this.player.addWeapon(new WeaponCard(Weapon.SHOCKWAVE));
        this.turnController = new TurnController(this.board, null, null);
        this.controller = new EffectsController(this.board, null);
        this.controller.setActivePlayer(this.player);
        List<Weapon> weapons = new ArrayList<>();
        weapons.add(Weapon.LOCK_RIFLE);
        weapons.add(Weapon.TRACTOR_BEAM);
        weapons.add(Weapon.CYBERBLADE);
        weapons.add(Weapon.ROCKET_LAUNCHER);
        weapons.add(Weapon.FURNACE);
        weapons.add(Weapon.VORTEX_CANNON);
        weapons.add(Weapon.RAILGUN);
        weapons.add(Weapon.SHOCKWAVE);
        assertEquals(weapons, this.controller.getAvailableWeapons());
    }

    @Test
    public void getAvailableEffectsTest() {
        List<WeaponEffectOrderType> availableEffects;
        availableEffects = new ArrayList<>();
        this.controller.setWeapon(Weapon.MACHINE_GUN);
        availableEffects.add(PRIMARY);
        assertTrue(availableEffects.containsAll(this.controller.getAvailableEffects().keySet()));
        availableEffects = new ArrayList<>();
        this.controller.setWeapon(Weapon.TRACTOR_BEAM);
        availableEffects.add(PRIMARY);
        availableEffects.add(ALTERNATIVE);
        assertTrue(availableEffects.containsAll(this.controller.getAvailableEffects().keySet()));
        availableEffects = new ArrayList<>();
        this.controller.setWeapon(Weapon.CYBERBLADE);
        availableEffects.add(PRIMARY);
        availableEffects.add(SECONDARYONE);
        assertTrue(availableEffects.containsAll(this.controller.getAvailableEffects().keySet()));
    }

    @Test
    public void damageCaseTest() {
        this.weapon = Weapon.LOCK_RIFLE;
        this.controller.setWeapon(weapon);
        this.controller.effectSelected(PRIMARY);
        this.characters.add(this.p1.getCharacter());
        this.characters.add(this.p2.getCharacter());
        assertEquals(this.characters, this.controller.seeEffectPossibility(weapon.getPrimaryEffect().get(0)).getCharacters());
    }

    @Test
    public void markCaseTest() {
        this.weapon = Weapon.LOCK_RIFLE;
        this.controller.setWeapon(weapon);
        this.controller.effectSelected(PRIMARY);
        this.controller.setHitByCases(p1.getCharacter());
        this.characters.add(this.p1.getCharacter());
        assertEquals(this.characters, this.controller.seeEffectPossibility(weapon.getPrimaryEffect().get(1)).getCharacters());
    }

    @Test
    public void moveSelfCaseTest() {
        this.weapon = Weapon.CYBERBLADE;
        this.controller.setWeapon(weapon);
        this.controller.effectSelected(SECONDARYONE);
        this.characters.add(this.player.getCharacter());
        this.squares.add(new Coordinates(2,0));
        this.squares.add(new Coordinates(1,1));
        assertEquals(this.characters, this.controller.seeEffectPossibility(weapon.getSecondaryEffectOne().get(0)).getCharacters());
        assertTrue(assertCoordinates(this.squares, this.controller.seeEffectPossibility(weapon.getSecondaryEffectOne().get(0)).getSquares()));

    }

    @Test
    public void mooveHitByMainCaseTest() {
        this.weapon = Weapon.ROCKET_LAUNCHER;
        this.controller.setWeapon(weapon);
        this.controller.effectSelected(PRIMARY);
        this.controller.setHitByCases(p1.getCharacter());
        this.characters.add(this.p1.getCharacter());
        this.squares.add(new Coordinates(1,0));
        this.squares.add(new Coordinates(2,1));
        assertEquals(this.characters, this.controller.seeEffectPossibility(weapon.getPrimaryEffect().get(1)).getCharacters());
        assertTrue(assertCoordinates(this.squares, this.controller.seeEffectPossibility(weapon.getPrimaryEffect().get(1)).getSquares()));

    }

    @Test
    public void mooveOtherCaseTest() {
        this.weapon = Weapon.TRACTOR_BEAM;
        this.controller.setWeapon(weapon);
        this.controller.effectSelected(ALTERNATIVE);
        this.characters.add(this.p1.getCharacter());
        this.characters.add(this.p2.getCharacter());
        this.squares.add(new Coordinates(1,0));
        assertEquals(this.characters, this.controller.seeEffectPossibility(weapon.getAlternativeMode().get(0)).getCharacters());
        assertTrue(assertCoordinates(this.squares, this.controller.seeEffectPossibility(weapon.getAlternativeMode().get(0)).getSquares()));
    }

    @Test
    public void selectSquareCaseTest() {
        this.weapon = Weapon.VORTEX_CANNON;
        this.controller.setWeapon(weapon);
        this.controller.effectSelected(PRIMARY);
        this.squares.add(new Coordinates(2,0));
        this.squares.add(new Coordinates(2,1));
        this.squares.add(new Coordinates(1,1));
        assertTrue(assertCoordinates(this.squares, this.controller.seeEffectPossibility(weapon.getPrimaryEffect().get(0)).getSquares()));
    }

    @Test
    public void selectRoomCaseTest() {
        this.weapon = Weapon.FURNACE;
        this.controller.setWeapon(weapon);
        this.controller.effectSelected(PRIMARY);
        this.rooms.add(RoomColor.PURPLE);
        assertEquals(this.rooms, this.controller.seeEffectPossibility(weapon.getPrimaryEffect().get(0)).getRooms());
    }

    @Test
    public void selectCardinalCaseTest() {
        this.weapon = Weapon.RAILGUN;
        this.controller.setWeapon(weapon);
        this.controller.effectSelected(PRIMARY);
        this.cardinalPoints.add(CardinalPoint.EAST);
        this.cardinalPoints.add(CardinalPoint.SOUTH);
        assertEquals(this.cardinalPoints, this.controller.seeEffectPossibility(weapon.getPrimaryEffect().get(0)).getCardinalPoints());
    }

    @Test
    public void selectMultipleSquareCaseTest() {
        this.weapon = Weapon.SHOCKWAVE;
        this.controller.setWeapon(weapon);
        this.controller.effectSelected(PRIMARY);
        List<GameCharacter> characters;
        characters = new ArrayList<>();
        characters.add(p1.getCharacter());
        this.multipleSquares.put(new Coordinates(2,0), characters);
        characters = new ArrayList<>();
        characters.add(p2.getCharacter());
        this.multipleSquares.put(new Coordinates(1,1), characters);
        assertTrue(assertMultipleSquare(this.multipleSquares, this.controller.seeEffectPossibility(weapon.getPrimaryEffect().get(0)).getMultipleSquares()));


    }

    private boolean assertCoordinates(List<Coordinates> coordinates1, List<Coordinates> coordinates2) {
        if(coordinates1.size() != coordinates2.size()) {
            return false;
        }
        for(Coordinates c1 : coordinates1) {
            boolean isEqual = false;
            for(Coordinates c2 : coordinates2) {
                if(c1.getX() == c2.getX() && c1.getY() == c2.getY()) {
                    isEqual = true;
                    break;
                }
            }
            if(!isEqual) {
                return false;
            }
        }
        return true;
    }

    private boolean assertMultipleSquare(Map<Coordinates, List<GameCharacter>> multi1, Map<Coordinates, List<GameCharacter>> multi2) {
        if(multi1.size() != multi2.size()) {
            return false;
        }
        for(Coordinates c1 : multi1.keySet()) {
            boolean isEqual = false;
            for(Coordinates c2 : multi2.keySet()) {
                if(c1.getX() == c2.getX() && c1.getY() == c2.getY() && multi1.get(c1).equals(multi2.get(c2))) {
                    isEqual = true;
                    break;
                }
            }
            if(!isEqual) {
                return false;
            }
        }
        return true;
    }
}
