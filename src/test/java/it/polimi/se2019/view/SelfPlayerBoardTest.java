package it.polimi.se2019.view;

import it.polimi.se2019.model.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

public class SelfPlayerBoardTest {

    private SelfPlayerBoard board;

    @Before
    public void SetUp() {
        this.board = new SelfPlayerBoard(GameCharacter.D_STRUCT_OR, "test");
    }

    @Test
    public void scoreTest() {
        assertEquals(0, this.board.getScore());
        this.board.raiseScore(4);
        assertEquals(4, this.board.getScore());
        this.board.raiseScore(1);
        assertEquals(5, this.board.getScore());
    }

    @Test
    public void powerupsTest() {
        Powerup powerup1 = new Powerup(PowerupType.NEWTON, AmmoType.YELLOW);
        this.board.addPowerup(powerup1);
        assertEquals(1, this.board.getPowerups().size());
        assertEquals(powerup1, this.board.getPowerups().get(0));
        this.board.removePowerup(powerup1.getType(), powerup1.getColor());
        assertEquals(0, this.board.getPowerups().size());
    }

    @Test
    public void weaponsTest() {
        Weapon weapon = Weapon.FURNACE;
        this.board.addWeapon(weapon);
        assertEquals(1, this.board.getWeaponsNumber());
        assertEquals(1, this.board.getReadyWeapons().size());
        assertEquals(weapon, this.board.getReadyWeapons().get(0));
        assertEquals(0, this.board.getUnloadedWeapons().size());
        this.board.unloadWeapon(weapon);
        assertEquals(1, this.board.getWeaponsNumber());
        assertEquals(1, this.board.getUnloadedWeapons().size());
        assertEquals(weapon, this.board.getUnloadedWeapons().get(0));
        assertEquals(0, this.board.getReadyWeapons().size());
        this.board.reloadWeapon(weapon);
        assertEquals(1, this.board.getReadyWeapons().size());
        assertEquals(weapon, this.board.getReadyWeapons().get(0));
        assertEquals(1, this.board.getWeaponsNumber());
        assertEquals(0, this.board.getUnloadedWeapons().size());
        this.board.unloadWeapon(weapon);
        this.board.removeWeapon(weapon);
        assertEquals(0, this.board.getWeaponsNumber());
        assertEquals(0, this.board.getUnloadedWeapons().size());
        assertEquals(0, this.board.getReadyWeapons().size());
    }

    @Test
    public void alternativeConstructorTest() {
        PlayerBoard b = new PlayerBoard(GameCharacter.D_STRUCT_OR, "test");
        b.addDamages(GameCharacter.BANSHEE, 5);
        b.addMarks(GameCharacter.VIOLET, 2);
        Map<AmmoType, Integer> ammos = new EnumMap<>(AmmoType.class);
        ammos.put(AmmoType.BLUE, 1);
        ammos.put(AmmoType.RED, 2);
        ammos.put(AmmoType.YELLOW, 0);
        b.addAmmos(ammos);
        b.addWeapon();
        b.addWeapon();
        b.addPowerup();
        b.addPowerup();
        b.unloadWeapon(Weapon.MACHINE_GUN);
        List<Weapon> readyWeapons = new ArrayList<>();
        readyWeapons.add(Weapon.SHOCKWAVE);
        List<Powerup> powerups = new ArrayList<>();
        powerups.add(new Powerup(PowerupType.TAGBACK_GRENADE, AmmoType.RED));
        powerups.add(new Powerup(PowerupType.TARGETING_SCOPE, AmmoType.BLUE));
        SelfPlayerBoard alternativeBoard = new SelfPlayerBoard(b, readyWeapons, powerups, 10);
        assertEquals(GameCharacter.D_STRUCT_OR, alternativeBoard.getCharacter());
        assertEquals("test", alternativeBoard.getNickname());
        assertEquals(b.getAvailableAmmos(), alternativeBoard.getAvailableAmmos());
        assertEquals(b.getRevengeMarks(), alternativeBoard.getRevengeMarks());
        assertEquals(b.getDamages(), alternativeBoard.getDamages());
        assertEquals(b.getKillshotPoints(), alternativeBoard.getKillshotPoints());
        assertEquals(b.getUnloadedWeapons(), alternativeBoard.getUnloadedWeapons());
        assertEquals(b.getWeaponsNumber(), alternativeBoard.getWeaponsNumber());
        assertEquals(b.getPowerupsNumber(), alternativeBoard.getPowerupsNumber());
        assertEquals(powerups, alternativeBoard.getPowerups());
        assertEquals(readyWeapons, alternativeBoard.getReadyWeapons());
        assertEquals(10, alternativeBoard.getScore());
    }

    @Test
    public void toStringTest() {
        assertEquals("Nickname:  test\n" +
                "Character: D_STRUCT_OR (α)\n" +
                "\n" +
                "Revenge marks: \n" +
                "\n" +
                "Damages:  _ _ | _ _ _ | _ _ _ _ _ | _ _ \n" +
                "Killshot: 8 6 4 2 1 1 \n" +
                "\n" +
                "Available ammos:    B R Y \n" +
                "Available powerups\n" +
                "Ready weapons:    \n" +
                "Unloaded weapons: \n" +
                "\n" +
                "Score: 0",
                this.board.toString());
    }
}
