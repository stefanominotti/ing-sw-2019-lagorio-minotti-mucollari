package it.polimi.se2019.view;

import it.polimi.se2019.model.*;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static junit.framework.TestCase.assertEquals;

public class PlayerBoardTest {

    private PlayerBoard board;

    @Before
    public void SetUp() {
        this.board = new PlayerBoard(GameCharacter.D_STRUCT_OR, "test");
    }

    @Test
    public void getCharacterTest() {
        assertEquals(GameCharacter.D_STRUCT_OR, this.board.getCharacter());
    }

    @Test
    public void getNicknameTest() {
        assertEquals("test", this.board.getNickname());
    }

    @Test
    public void killshotPointsTest() {
        assertEquals(Arrays.asList(8, 6, 4, 2, 1, 1), this.board.getKillshotPoints());
        this.board.reduceKillshotPoints();
        assertEquals(Arrays.asList(6, 4, 2, 1, 1), this.board.getKillshotPoints());
        this.board.flipBoard();
        assertEquals(Arrays.asList(2, 1, 1, 1), this.board.getKillshotPoints());
    }

    @Test
    public void weaponsTest() {
        Weapon weapon = Weapon.FURNACE;
        this.board.addWeapon();
        assertEquals(1, this.board.getWeaponsNumber());
        this.board.unloadWeapon(weapon);
        assertEquals(1, this.board.getWeaponsNumber());
        assertEquals(1, this.board.getUnloadedWeapons().size());
        assertEquals(weapon, this.board.getUnloadedWeapons().get(0));
        this.board.reloadWeapon(weapon);
        assertEquals(1, this.board.getWeaponsNumber());
        assertEquals(0, this.board.getUnloadedWeapons().size());
        this.board.unloadWeapon(weapon);
        this.board.removeWeapon(weapon);
        assertEquals(0, this.board.getWeaponsNumber());
        assertEquals(0, this.board.getUnloadedWeapons().size());
    }

    @Test
    public void revengeMarksTest() {
        this.board.addMarks(GameCharacter.BANSHEE, 3);
        this.board.addMarks(GameCharacter.VIOLET, 1);
        List<GameCharacter> marks = new ArrayList<>(Arrays.asList(GameCharacter.BANSHEE, GameCharacter.BANSHEE,
                GameCharacter.BANSHEE, GameCharacter.VIOLET));
        assertEquals(marks, this.board.getRevengeMarks());
        this.board.resetMarks(GameCharacter.VIOLET);
        marks.remove(GameCharacter.VIOLET);
        assertEquals(marks, this.board.getRevengeMarks());
        this.board.resetMarks(GameCharacter.BANSHEE);
        for (int i = 0; i < 3; i++) {
            marks.remove(GameCharacter.BANSHEE);
        }
        assertEquals(marks, this.board.getRevengeMarks());
    }

    @Test
    public void ammosTest() {
        Map<AmmoType, Integer> toAdd = new EnumMap<>(AmmoType.class);
        toAdd.put(AmmoType.BLUE, 1);
        toAdd.put(AmmoType.RED, 2);
        toAdd.put(AmmoType.YELLOW, 0);
        this.board.addAmmos(toAdd);
        Map<AmmoType, Integer> ammos = new EnumMap<>(AmmoType.class);
        ammos.put(AmmoType.BLUE, 2);
        ammos.put(AmmoType.RED, 3);
        ammos.put(AmmoType.YELLOW, 1);
        assertEquals(ammos, this.board.getAvailableAmmos());
        Map<AmmoType, Integer> toRemove = new EnumMap<>(AmmoType.class);
        toRemove.put(AmmoType.BLUE, 0);
        toRemove.put(AmmoType.RED, 2);
        toRemove.put(AmmoType.YELLOW, 1);
        this.board.useAmmos(toRemove);
        ammos.put(AmmoType.BLUE, 2);
        ammos.put(AmmoType.RED, 1);
        ammos.put(AmmoType.YELLOW, 0);
        assertEquals(ammos, this.board.getAvailableAmmos());
    }

    @Test
    public void damagesTest() {
        this.board.addDamages(GameCharacter.BANSHEE, 5);
        this.board.addDamages(GameCharacter.VIOLET, 4);
        List<GameCharacter> damage = Arrays.asList(GameCharacter.BANSHEE, GameCharacter.BANSHEE, GameCharacter.BANSHEE,
                GameCharacter.BANSHEE, GameCharacter.BANSHEE, GameCharacter.VIOLET, GameCharacter.VIOLET,
                GameCharacter.VIOLET, GameCharacter.VIOLET);
        assertEquals(damage, this.board.getDamages());
        this.board.resetDamages();
        assertEquals(0, this.board.getDamages().size());
    }

    @Test
    public void powerupsTest() {
        this.board.addPowerup();
        assertEquals(1, this.board.getPowerupsNumber());
        this.board.removePowerup();
        assertEquals(0, this.board.getPowerupsNumber());
    }

    @Test
    public void alternativeConstructorTest() {
        this.board.addDamages(GameCharacter.BANSHEE, 5);
        this.board.addMarks(GameCharacter.VIOLET, 2);
        Map<AmmoType, Integer> toAdd = new EnumMap<>(AmmoType.class);
        toAdd.put(AmmoType.BLUE, 1);
        toAdd.put(AmmoType.RED, 2);
        toAdd.put(AmmoType.YELLOW, 0);
        this.board.addAmmos(toAdd);
        List<Weapon> unloadedWeapons = new ArrayList<>();
        unloadedWeapons.add(Weapon.MACHINE_GUN);
        PlayerBoard alternativeBoard = new PlayerBoard(GameCharacter.DOZER, "alternative",
                this.board.getAvailableAmmos(), this.board.getRevengeMarks(), this.board.getDamages(),
                this.board.getKillshotPoints(), unloadedWeapons, 3, 2);
        assertEquals(GameCharacter.DOZER, alternativeBoard.getCharacter());
        assertEquals("alternative", alternativeBoard.getNickname());
        assertEquals(this.board.getAvailableAmmos(), alternativeBoard.getAvailableAmmos());
        assertEquals(this.board.getRevengeMarks(), alternativeBoard.getRevengeMarks());
        assertEquals(this.board.getDamages(), alternativeBoard.getDamages());
        assertEquals(this.board.getKillshotPoints(), alternativeBoard.getKillshotPoints());
        assertEquals(unloadedWeapons, alternativeBoard.getUnloadedWeapons());
        assertEquals(3, alternativeBoard.getWeaponsNumber());
        assertEquals(2, alternativeBoard.getPowerupsNumber());
    }
}
