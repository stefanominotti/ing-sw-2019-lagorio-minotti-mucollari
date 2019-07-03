package it.polimi.se2019.model;

import it.polimi.se2019.model.arena.Square;
import it.polimi.se2019.model.playerassets.AmmoType;
import it.polimi.se2019.model.playerassets.Powerup;
import it.polimi.se2019.model.playerassets.PowerupType;
import it.polimi.se2019.model.playerassets.weapons.Weapon;
import it.polimi.se2019.model.playerassets.weapons.WeaponCard;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static junit.framework.TestCase.*;
import static org.junit.Assert.assertNull;

public class PlayerTest {

    private static final GameCharacter CHARACTER = GameCharacter.BANSHEE;
    private Board board;
    private Player player;

    @Before
    public void setUp() {
        this.board = new Board();
        this.board.addPlayer(CHARACTER, "testNickname", "token");
        this.player = this.board.getPlayerByCharacter(CHARACTER);
    }

    @Test
    public void toJsonTest() {
        assertEquals("{\"token\": \"token\",\"name\": \"testNickname\",\"character\": \"BANSHEE\"," +
                "\"score\": 0,\"dead\": false,\"connected\": false,\"damages\": []," +
                "\"killshotPoints\": [8,6,4,2,1,1],\"availableAmmos\": {\"BLUE\":1,\"RED\":1,\"YELLOW\":1}," +
                "\"revengeMarks\": [],\"weapons\": [],\"powerups\": []}", this.player.toJson());
    }

    @Test
    public void getCharacterTest() {
        assertEquals(CHARACTER, this.player.getCharacter());
    }

    @Test
    public void verifyPlayerTest() {
        assertEquals(CHARACTER, this.player.verifyPlayer("token"));
        assertNull(this.player.verifyPlayer("abcdef"));
    }

    @Test
    public void getNicknameTest() {
        assertEquals("testNickname", this.player.getNickname());
    }

    @Test
    public void connectionTest() {
        this.player.connect();
        assertTrue(this.player.isConnected());
        this.player.disconnect();
        assertFalse(this.player.isConnected());
    }

    @Test
    public void killshotPointsTest() {
        List<Integer> killshotPoints = Arrays.asList(8, 6, 4, 2, 1, 1);
        assertEquals(killshotPoints, this.player.getKillshotPoints());
        this.player.reduceKillshotPoints();
        killshotPoints = Arrays.asList(6, 4, 2, 1, 1);
        assertEquals(killshotPoints, this.player.getKillshotPoints());
        for (int i = 0; i < 6; i++) {
            this.player.reduceKillshotPoints();
        }
        assertEquals(0, this.player.getKillshotPoints().size());
    }

    @Test
    public void damagesTest() {
        GameCharacter enemyChar1 = GameCharacter.D_STRUCT_OR;
        this.board.addPlayer(enemyChar1, "testNickname1", "token");
               this.player.addDamages(enemyChar1, 5);
        GameCharacter enemyChar2 = GameCharacter.BANSHEE;
        this.board.addPlayer(enemyChar2, "testNickname2", "token");
        this.player.addDamages(enemyChar2, 4);
        List<GameCharacter> damage = Arrays.asList(enemyChar1, enemyChar1, enemyChar1, enemyChar1, enemyChar1,
                enemyChar2, enemyChar2, enemyChar2, enemyChar2);
        assertEquals(damage, this.player.getDamages());
        this.player.resetDamages();
        assertEquals(0, this.player.getDamages().size());

    }

    @Test
    public void deathTest() {
        this.player.setDead(true);
        assertTrue(this.player.isDead());
        this.player.setDead(false);
        assertFalse(this.player.isDead());
    }

    @Test
    public void weaponsTest() {
        WeaponCard weapon = new WeaponCard(Weapon.FURNACE);
        this.player.addWeapon(weapon);
        assertEquals(1, this.player.getWeapons().size());
        assertEquals(weapon, this.player.getWeapons().get(0));
        this.player.removeWeapon(weapon);
        assertEquals(new ArrayList<WeaponCard>(), this.player.getWeapons());
    }

    @Test
    public void revengeMarksTest() {
        GameCharacter enemyChar1 = GameCharacter.D_STRUCT_OR;
        this.board.addPlayer(enemyChar1, "testNickname", "token");
        GameCharacter enemyChar2 = GameCharacter.DOZER;
        this.board.addPlayer(enemyChar2, "testNickname2", "token");
        this.player.addRevengeMarks(enemyChar1, 3);
        this.player.addRevengeMarks(enemyChar2, 1);
        List<GameCharacter> marks = new ArrayList<>(Arrays.asList(enemyChar1, enemyChar1, enemyChar1, enemyChar2));
        assertEquals(marks, this.player.getRevengeMarks());
        assertEquals(3, this.player.getMarksNumber(enemyChar1));
        this.player.resetMarks(enemyChar2);
        marks.remove(enemyChar2);
        assertEquals(marks, this.player.getRevengeMarks());
        this.player.resetMarks(enemyChar1);
        for (int i = 0; i< 3; i++) {
            marks.remove(enemyChar1);
        }
        assertEquals(marks, this.player.getRevengeMarks());
    }

    @Test
    public void ammosTest() {
        Map<AmmoType, Integer> toAdd = new EnumMap<>(AmmoType.class);
        toAdd.put(AmmoType.BLUE, 1);
        toAdd.put(AmmoType.RED, 2);
        toAdd.put(AmmoType.YELLOW, 0);
        this.player.addAmmos(toAdd);
        Map<AmmoType, Integer> ammos = new EnumMap<>(AmmoType.class);
        ammos.put(AmmoType.BLUE, 2);
        ammos.put(AmmoType.RED, 3);
        ammos.put(AmmoType.YELLOW, 1);
        assertEquals(ammos, this.player.getAvailableAmmos());
        Map<AmmoType, Integer> toRemove = new EnumMap<>(AmmoType.class);
        toRemove.put(AmmoType.BLUE, 0);
        toRemove.put(AmmoType.RED, 2);
        toRemove.put(AmmoType.YELLOW, 1);
        this.player.removeAmmos(toRemove);
        ammos.put(AmmoType.BLUE, 2);
        ammos.put(AmmoType.RED, 1);
        ammos.put(AmmoType.YELLOW, 0);
        assertEquals(ammos, this.player.getAvailableAmmos());
    }

    @Test
    public void scoreTest() {
        this.player.raiseScore(4);
        assertEquals(4, this.player.getScore());
        this.player.raiseScore(1);
        assertEquals(5, this.player.getScore());
    }

    @Test
    public void powerupsTest() {
        Powerup powerup1 = new Powerup(PowerupType.NEWTON, AmmoType.YELLOW);
        this.player.addPowerup(powerup1);
        assertEquals(1, this.player.getPowerups().size());
        assertEquals(powerup1, this.player.getPowerups().get(0));
        this.player.removePowerup(powerup1);
        assertEquals(new ArrayList<Powerup>(), this.player.getPowerups());
        this.player.addPowerup(powerup1);
        assertEquals(powerup1, this.player.getPowerupByType(PowerupType.NEWTON, AmmoType.YELLOW));
        Powerup powerup2 = new Powerup(PowerupType.NEWTON, AmmoType.RED);
        this.player.addPowerup(powerup2);
        assertEquals(powerup1, this.player.getPowerupsByType(PowerupType.NEWTON).get(0));
        assertEquals(powerup2, this.player.getPowerupsByType(PowerupType.NEWTON).get(1));
    }

    @Test
    public void positionTest() {
        this.board.loadArena("1");
        Square square = this.board.getArena().getAllSquares().get(0);
        this.player.setPosition(square);
        assertEquals(square, this.player.getPosition());
    }

    @Test
    public void flipBoardTest() {
        List<Integer> killPoints = Arrays.asList(2, 1, 1, 1);
        this.player.flipBoard();
        assertEquals(killPoints, this.player.getKillshotPoints());
    }
}