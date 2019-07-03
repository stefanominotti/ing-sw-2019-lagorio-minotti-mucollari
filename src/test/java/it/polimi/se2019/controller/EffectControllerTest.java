package it.polimi.se2019.controller;

import it.polimi.se2019.model.*;
import it.polimi.se2019.model.arena.CardinalPoint;
import it.polimi.se2019.model.arena.Coordinates;
import it.polimi.se2019.model.arena.RoomColor;
import it.polimi.se2019.model.messages.turn.TurnMessage;
import it.polimi.se2019.model.messages.turn.TurnMessageType;
import it.polimi.se2019.model.playerassets.AmmoType;
import it.polimi.se2019.model.playerassets.Powerup;
import it.polimi.se2019.model.playerassets.PowerupType;
import it.polimi.se2019.model.playerassets.weapon.EffectType;
import it.polimi.se2019.model.playerassets.weapon.Weapon;
import it.polimi.se2019.model.playerassets.weapon.WeaponCard;
import it.polimi.se2019.model.playerassets.weapon.WeaponEffectOrderType;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static it.polimi.se2019.model.playerassets.weapon.WeaponEffectOrderType.*;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;


public class EffectControllerTest {
    private Weapon weapon;
    private Board board;
    private Player player;
    private Player p1;
    private Player p2;
    private EffectsController controller;
    private GameController gameController;

    // For pack
    private List<GameCharacter> characters;
    private List<Coordinates> squares;
    private List<RoomColor> rooms;
    private List<CardinalPoint> cardinalPoints;
    private Map<Coordinates, List<GameCharacter>> multipleSquares;

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
        this.player.addWeapon(new WeaponCard(Weapon.MACHINE_GUN));
        this.player.addWeapon(new WeaponCard(Weapon.LOCK_RIFLE));
        this.player.addWeapon(new WeaponCard(Weapon.TRACTOR_BEAM));
        this.player.addWeapon(new WeaponCard(Weapon.CYBERBLADE));
        this.player.addWeapon(new WeaponCard(Weapon.ROCKET_LAUNCHER));
        this.player.addWeapon(new WeaponCard(Weapon.FURNACE));
        this.player.addWeapon(new WeaponCard(Weapon.VORTEX_CANNON));
        this.player.addWeapon(new WeaponCard(Weapon.RAILGUN));
        this.player.addWeapon(new WeaponCard(Weapon.SHOCKWAVE));
        this.player.addWeapon(new WeaponCard(Weapon.HEATSEEKER));
        this.player.addWeapon(new WeaponCard(Weapon.POWER_GLOVE));
        this.player.addWeapon(new WeaponCard(Weapon.SLEDGEHAMMER));
        this.player.addPowerup(new Powerup(PowerupType.NEWTON, AmmoType.BLUE));
        this.gameController = new GameController(this.board, null);
        this.controller = new EffectsController(this.board, this.gameController);
        this.controller.setActivePlayer(this.player);
        List<Weapon> weapons = new ArrayList<>();
        weapons.add(Weapon.MACHINE_GUN);
        weapons.add(Weapon.LOCK_RIFLE);
        weapons.add(Weapon.TRACTOR_BEAM);
        weapons.add(Weapon.CYBERBLADE);
        weapons.add(Weapon.ROCKET_LAUNCHER);
        weapons.add(Weapon.FURNACE);
        weapons.add(Weapon.VORTEX_CANNON);
        weapons.add(Weapon.RAILGUN);
        weapons.add(Weapon.SHOCKWAVE);
        weapons.add(Weapon.POWER_GLOVE);
        assertEquals(weapons, this.controller.getAvailableWeapons());
        this.gameController.update(null,
                new TurnMessage(TurnMessageType.START, TurnType.FIRST_TURN, this.player.getCharacter()));
    }

    @Test
    public void effectPossibiltyPackTest() {
        this.weapon = Weapon.MACHINE_GUN;
        this.controller.setWeapon(weapon);
        this.controller.selectEffect(PRIMARY);
        EffectPossibilityPack pack = this.controller.seeEffectPossibility(this.weapon.getPrimaryEffect().get(0));
        assertEquals("1", pack.getTargetsAmount().get(0));
        assertEquals("2", pack.getTargetsAmount().get(1));
        assertEquals(EffectType.DAMAGE, pack.getType());
        assertNotNull(pack.getDescription());
        this.characters = new ArrayList<>(Arrays.asList(this.p1.getCharacter()));
        this.squares = new ArrayList<>(Arrays.asList(new Coordinates(0,0)));
        this.rooms = new ArrayList<>(Arrays.asList(RoomColor.RED));
        this.cardinalPoints = new ArrayList<>(Arrays.asList(CardinalPoint.EAST));
        pack = new EffectPossibilityPack(false, EffectType.DAMAGE);
        pack.setCharacters(this.characters);
        pack.setSquares(this.squares);
        pack.setRooms(this.rooms);
        pack.setCardinalPoints(this.cardinalPoints);
        pack.setRequire(false);
        assertEquals(this.characters, pack.getCharacters());
        assertEquals(this.squares, pack.getSquares());
        assertEquals(this.rooms, pack.getRooms());
        assertEquals(this.cardinalPoints, pack.getCardinalPoints());
        assertFalse(pack.isRequire());
    }

    @Test
    public void getAvailableEffectsTest() {
        List<WeaponEffectOrderType> availableEffects;
        availableEffects = new ArrayList<>();
        this.controller.setWeapon(Weapon.LOCK_RIFLE);
        availableEffects.add(PRIMARY);

        assertTrue(availableEffects.containsAll(this.controller.getAvailableEffects().keySet()));
        availableEffects = new ArrayList<>();
        this.controller.setWeapon(Weapon.TRACTOR_BEAM);
        availableEffects.add(PRIMARY);
        availableEffects.add(ALTERNATIVE);
        assertTrue(availableEffects.containsAll(this.controller.getAvailableEffects().keySet()));
        availableEffects = new ArrayList<>();
        this.controller.setWeapon(Weapon.CYBERBLADE);
        availableEffects.add(SECONDARYONE);
        assertTrue(availableEffects.containsAll(this.controller.getAvailableEffects().keySet()));

        this.player.setPosition(this.board.getArena().getSquareByCoordinate(0,0));
        this.weapon = Weapon.CYBERBLADE;
        this.controller.setWeapon(weapon);
        assertTrue(this.controller.getAvailableEffects().keySet().isEmpty());
    }

    @Test
    public void damageCaseTest() {
        this.weapon = Weapon.ROCKET_LAUNCHER;
        this.controller.setWeapon(weapon);
        this.controller.selectEffect(PRIMARY);
        this.characters.add(this.p1.getCharacter());
        this.characters.add(this.p2.getCharacter());
        EffectPossibilityPack pack = this.controller.seeEffectPossibility(weapon.getPrimaryEffect().get(0));
        assertEquals(this.characters, pack.getCharacters());
        pack.setCharacters(new ArrayList<>(Arrays.asList(this.p1.getCharacter())));
        this.controller.application(pack);
        this.controller.effectSelected(SECONDARYTWO);
        this.characters.remove(this.p2.getCharacter());
        assertEquals(this.characters, this.controller.seeEffectPossibility(weapon.getSecondaryEffectTwo().get(0)).getCharacters());
        this.characters = new ArrayList<>(Arrays.asList(p2.getCharacter()));
        this.weapon = Weapon.FURNACE;
        this.controller.setWeapon(weapon);
        this.controller.selectEffect(PRIMARY);
        this.controller.setCurrentEffect(this.weapon.getPrimaryEffect().get(0));
        this.controller.application(this.controller.seeEffectPossibility(this.weapon.getPrimaryEffect().get(0)));
        assertEquals(this.characters, this.controller.seeEffectPossibility(this.weapon.getPrimaryEffect().get(1)).getCharacters());
        this.weapon = Weapon.MACHINE_GUN;
        this.controller.setWeapon(weapon);
        this.controller.selectEffect(PRIMARY);
        this.controller.setCurrentEffect(this.weapon.getPrimaryEffect().get(0));
        this.controller.application(this.controller.seeEffectPossibility(this.weapon.getPrimaryEffect().get(0)));
        this.controller.selectEffect(SECONDARYONE);
        this.controller.setCurrentEffect(this.weapon.getSecondaryEffectOne().get(0));
        pack = this.controller.seeEffectPossibility(this.weapon.getSecondaryEffectOne().get(0));
        pack.setCharacters(new ArrayList<>(Arrays.asList(p1.getCharacter())));
        this.characters = new ArrayList<>(Arrays.asList(this.p2.getCharacter()));
        this.controller.application(pack);
        this.controller.selectEffect(SECONDARYTWO);
        assertEquals(this.characters, this.controller.seeEffectPossibility(this.weapon.getSecondaryEffectTwo().get(0)).getCharacters());

    }

    @Test(expected = UnsupportedOperationException.class)
    public void damageExeptionCaseTest() {
        this.weapon = Weapon.HEATSEEKER;
        this.controller.setWeapon(weapon);
        this.controller.selectEffect(PRIMARY);
        this.controller.seeEffectPossibility(weapon.getPrimaryEffect().get(0));
    }

    @Test
    public void markCaseTest() {
        this.weapon = Weapon.LOCK_RIFLE;
        this.controller.setWeapon(weapon);
        this.controller.selectEffect(PRIMARY);
        this.controller.setHitByCases(p1.getCharacter());
        this.characters.add(this.p1.getCharacter());
        assertEquals(this.characters, this.controller.seeEffectPossibility(weapon.getPrimaryEffect().get(1)).getCharacters());
    }

    @Test
    public void moveSelfCaseTest() {
        this.weapon = Weapon.CYBERBLADE;
        this.controller.setWeapon(weapon);
        this.controller.selectEffect(SECONDARYONE);
        this.characters.add(this.player.getCharacter());
        this.squares.add(new Coordinates(2,0));
        this.squares.add(new Coordinates(1,1));
        assertEquals(this.characters, this.controller.seeEffectPossibility(weapon.getSecondaryEffectOne().get(0)).getCharacters());
        assertTrue(assertCoordinates(this.squares,
                this.controller.seeEffectPossibility(weapon.getSecondaryEffectOne().get(0)).getSquares()));
        this.weapon = Weapon.POWER_GLOVE;
        this.controller.setWeapon(weapon);
        this.controller.selectEffect(PRIMARY);
        this.controller.setEnvironment(this.player.getPosition(), this.board.getArena().getSquareByCoordinate(1,1));
        this.squares = new ArrayList<>(Arrays.asList(new Coordinates(1,1)));
        assertEquals(this.characters, this.controller.seeEffectPossibility(weapon.getPrimaryEffect().get(1)).getCharacters());
        assertTrue(assertCoordinates(this.squares,
                this.controller.seeEffectPossibility(weapon.getPrimaryEffect().get(1)).getSquares()));
        this.controller.setCurrentEffect(this.weapon.getPrimaryEffect().get(1));
        this.controller.application(this.controller.seeEffectPossibility(this.weapon.getPrimaryEffect().get(1)));
        assertEquals(this.board.getArena().getSquareByCoordinate(1,1),
                this.player.getPosition());


    }

    @Test
    public void moveHitByMainCaseTest() {
        this.weapon = Weapon.ROCKET_LAUNCHER;
        this.controller.setWeapon(weapon);
        this.controller.selectEffect(PRIMARY);
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
        this.controller.selectEffect(ALTERNATIVE);
        this.characters.add(this.p1.getCharacter());
        this.characters.add(this.p2.getCharacter());
        this.squares.add(new Coordinates(1,0));
        assertEquals(this.characters, this.controller.seeEffectPossibility(this.weapon.getAlternativeMode().get(0)).getCharacters());
        assertTrue(assertCoordinates(this.squares,
                this.controller.seeEffectPossibility(weapon.getAlternativeMode().get(0)).getSquares()));
        this.player.setPosition(this.board.getArena().getSquareByCoordinate(1,1));
        this.weapon = Weapon.SLEDGEHAMMER;
        this.controller.setWeapon(weapon);
        this.controller.selectEffect(ALTERNATIVE);
        this.p1.isDead();
        this.controller.setCurrentEffect(this.weapon.getAlternativeMode().get(0));
        this.controller.application(this.controller.seeEffectPossibility(this.weapon.getAlternativeMode().get(0)));
        this.controller.setCurrentEffect(this.weapon.getAlternativeMode().get(1));
        EffectPossibilityPack pack = this.controller.seeEffectPossibility(this.weapon.getAlternativeMode().get(1));
        pack.setRequire(true);
        this.controller.application(pack);
        this.squares.remove(0);
        this.squares.add(new Coordinates(1,0));
        assertTrue(assertCoordinates(this.squares,
                this.controller.seeEffectPossibility(weapon.getAlternativeMode().get(2)).getSquares()));
        this.controller.setCurrentEffect(this.weapon.getAlternativeMode().get(2));
        this.controller.application(this.controller.seeEffectPossibility(this.weapon.getAlternativeMode().get(2)));
        assertEquals(this.board.getArena().getSquareByCoordinate(1,0),
                this.p2.getPosition());
    }

    @Test
    public void selectSquareCaseTest() {
        this.weapon = Weapon.VORTEX_CANNON;
        this.controller.setWeapon(weapon);
        this.controller.selectEffect(PRIMARY);
        this.squares.add(new Coordinates(2,0));
        this.squares.add(new Coordinates(2,1));
        this.squares.add(new Coordinates(1,1));
        assertTrue(assertCoordinates(this.squares,
                this.controller.seeEffectPossibility(weapon.getPrimaryEffect().get(0)).getSquares()));
        this.controller.setEnvironment(this.player.getPosition(), this.board.getArena().getSquareByCoordinate(1,1));
        this.characters.add(p2.getCharacter());
        assertEquals(this.characters, this.controller.seeEffectPossibility(weapon.getPrimaryEffect().get(1)).getCharacters());
    }

    @Test
    public void selectRoomCaseTest() {
        this.weapon = Weapon.FURNACE;
        this.controller.setWeapon(weapon);
        this.controller.selectEffect(PRIMARY);
        this.rooms.add(RoomColor.PURPLE);
        assertEquals(this.rooms, this.controller.seeEffectPossibility(weapon.getPrimaryEffect().get(0)).getRooms());
    }

    @Test
    public void selectCardinalCaseTest() {
        this.weapon = Weapon.RAILGUN;
        this.controller.setWeapon(weapon);
        this.controller.selectEffect(PRIMARY);
        this.cardinalPoints.add(CardinalPoint.EAST);
        this.cardinalPoints.add(CardinalPoint.SOUTH);
        assertEquals(this.cardinalPoints, this.controller.seeEffectPossibility(weapon.getPrimaryEffect().get(0)).getCardinalPoints());
        this.controller.setEnvironment(this.player.getPosition(), this.board.getArena().getSquareByCoordinate(1,1));
        this.characters.add(p2.getCharacter());
        assertEquals(this.characters, this.controller.seeEffectPossibility(this.weapon.getPrimaryEffect().get(1)).getCharacters());
    }

    @Test
    public void selectMultipleSquareCaseTest() {
        this.weapon = Weapon.SHOCKWAVE;
        this.controller.setWeapon(weapon);
        this.controller.selectEffect(PRIMARY);
        List<GameCharacter> characters;
        characters = new ArrayList<>();
        characters.add(p1.getCharacter());
        this.multipleSquares.put(new Coordinates(2,0), characters);
        characters = new ArrayList<>();
        characters.add(p2.getCharacter());
        this.multipleSquares.put(new Coordinates(1,1), characters);
        assertTrue(assertMultipleSquare(this.multipleSquares, this.controller.seeEffectPossibility(weapon.getPrimaryEffect().get(0)).getMultipleSquares()));
    }

    @Test
    public void checkCostTest() {
        assertTrue(this.controller.checkCost(this.player.getWeapons().get(0).getWeaponType().getSecondaryEffectOne()));
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
