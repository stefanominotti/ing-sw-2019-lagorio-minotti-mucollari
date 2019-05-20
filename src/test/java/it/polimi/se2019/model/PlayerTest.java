package it.polimi.se2019.model;

import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static junit.framework.TestCase.assertEquals;

public class PlayerTest {

    private static final GameCharacter CHARACTER = GameCharacter.BANSHEE;
    private Board board;
    private Player player;

    @Before
    public void setUp() {
        this.board = new Board();
        this.board.addPlayer(CHARACTER, "testNickname");
        this.player = this.board.getPlayerByCharacter(CHARACTER);
    }

    @Test
    public void getCharacterTest() {
        assertEquals(CHARACTER, this.player.getCharacter());
    }


    @Test
    public void damagesTest() {
        GameCharacter enemyChar = GameCharacter.D_STRUCT_OR;
        this.board.addPlayer(enemyChar, "testNickname");
               this.player.addDamages(enemyChar, 5);
        List<GameCharacter> damage = Arrays.asList(enemyChar, enemyChar, enemyChar, enemyChar, enemyChar);
        assertEquals(damage, this.player.getDamages());
    }

    @Test
    public void weaponsTest() {
        WeaponCard weapon = new WeaponCard(Weapon.FURNACE);
        this.player.addWeapon(weapon);
        List<WeaponCard> weapons = Arrays.asList(weapon);
        assertEquals(weapons, this.player.getWeapons());
        this.player.removeWeapon(weapon);
        assertEquals(new ArrayList<WeaponCard>(), this.player.getWeapons());
    }

    @Test
    public void revengeMarksTest() {
        GameCharacter enemyChar1 = GameCharacter.D_STRUCT_OR;
        this.board.addPlayer(enemyChar1, "testNickname");
        GameCharacter enemyChar2 = GameCharacter.DOZER;
        this.board.addPlayer(enemyChar2, "testNickname2");
        this.player.addRevengeMarks(enemyChar1, 3);
        this.player.addRevengeMarks(enemyChar2, 1);
        Map<GameCharacter, Integer> marks = new HashMap<>();
        marks.put(enemyChar1, 3);
        marks.put(enemyChar2, 1);
        assertEquals(marks, this.player.getRevengeMarks());
        this.player.marksToDamages(enemyChar2);
        marks.remove(enemyChar2);
        assertEquals(marks, this.player.getRevengeMarks());
        List<GameCharacter> damage = Arrays.asList(enemyChar2);
        assertEquals(damage, this.player.getDamages());
        this.player.marksToDamages(enemyChar1);
        marks.remove(enemyChar1);
        assertEquals(marks, this.player.getRevengeMarks());
        damage = Arrays.asList(enemyChar2, enemyChar1, enemyChar1, enemyChar1);
        assertEquals(damage, this.player.getDamages());
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
        Powerup powerup = new Powerup(PowerupType.NEWTON, AmmoType.YELLOW);
        this.player.addPowerup(powerup);
        List<Powerup> powerups = Arrays.asList(powerup);
        assertEquals(powerups, this.player.getPowerups());
        this.player.removePowerup(powerup);
        assertEquals(new ArrayList<Powerup>(), this.player.getPowerups());
    }
    @Test
    public void getPowerupsNumberTest() {
        Powerup powerup = new Powerup(PowerupType.NEWTON, AmmoType.YELLOW);
        Powerup powerup1 = new Powerup(PowerupType.TAGBACK_GRENADE, AmmoType.BLUE);
        int number = 2;
        this.player.addPowerup(powerup);
        this.player.addPowerup(powerup1);
        assertEquals(number, player.getPowerups().size());
        assertEquals(number, player.getPowerupsNumber());
    }
}