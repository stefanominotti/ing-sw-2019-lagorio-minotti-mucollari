package it.polimi.se2019.server;

import it.polimi.se2019.model.*;
import org.junit.Before;
import org.junit.Test;
import org.omg.PortableServer.LIFESPAN_POLICY_ID;

import java.io.FileNotFoundException;
import java.util.*;

import static it.polimi.se2019.model.PowerupType.TARGETING_SCOPE;
import static org.junit.Assert.*;

public class GameLoaderTest {
    
    Board board;
    GameLoader gameLoader;

    @Before
    public void setUp() {
        this.gameLoader = new GameLoader();
        this.board = gameLoader.loadBoard();
    }

    @Test
    public void gameLoaderTest() {
        //board
        assertNotNull(this.board);
        assertNotNull(this.board.getPlayers());
        assertNotNull(this.board.getArena());
        assertNotNull(this.board.getArena().getAllSquares());
        assertNotNull(this.board.getPlayers().get(0).getPowerups());
        //player
        /*assertEquals(4, this.board.getSkulls());
        assertEquals(GameState.FIRST_TURN, this.board.getGameState());
        assertEquals(GameCharacter.D_STRUCT_OR, this.board.getPlayers().get(0).getCharacter());
        assertEquals("primo" ,this.board.getPlayers().get(0).getNickname());
        assertEquals(0 ,this.board.getPlayers().get(0).getScore());
        assertEquals(availableAmmos() ,this.board.getPlayers().get(0).getAvailableAmmos());
        assertEquals(damages() ,this.board.getPlayers().get(0).getDamages());
        assertEquals(killshotPoints() ,this.board.getPlayers().get(0).getKillshotPoints());
        assertEquals(position() ,this.board.getPlayers().get(0).getPosition());
        assertEquals(revengeMarks() ,this.board.getPlayers().get(0).getRevengeMarks());
        assertEquals(weapons() ,this.board.getPlayers().get(0).getWeapons());
        for(int i = 0; i < powerups().size(); i++) {
            assertEquals(powerups().get(i).getType(), this.board.getPlayers().get(i).getPowerups().get(i).getType());
            assertEquals(powerups().get(i).getColor(), this.board.getPlayers().get(i).getPowerups().get(i).getColor());
        }
        assertFalse(this.board.getPlayers().get(0).isDead());
        //square
        assertEquals(squareTile().hasPowerup(),
                this.board.getArena().getSquareByCoordinate(0,0).getAvailableAmmoTile().hasPowerup());
        assertEquals(squareTile().getAmmos(),
                this.board.getArena().getSquareByCoordinate(0,0).getAvailableAmmoTile().getAmmos());
        assertEquals(squareWeapon().getOwner(),
                this.board.getArena().getSquareByCoordinate(2,0).getWeaponsStore().get(0).getOwner());
        assertEquals(squareWeapon().getWeaponType(),
                this.board.getArena().getSquareByCoordinate(2,0).getWeaponsStore().get(0).getWeaponType());
        assertEquals(squareWeapon().isReady(),
                this.board.getArena().getSquareByCoordinate(2,0).getWeaponsStore().get(0).isReady());*/
    }

    private Map<AmmoType, Integer> availableAmmos() {
        Map<AmmoType, Integer> ammos = new HashMap<>();
        ammos.put(AmmoType.BLUE,1);
        ammos.put(AmmoType.RED,1);
        ammos.put(AmmoType.YELLOW,1);
        return ammos;
    }

    private List<GameCharacter> damages() {
        List<GameCharacter> damages = new ArrayList<>();
        return damages;
    }

    private List<Integer> killshotPoints() {
        List<Integer> points = new ArrayList<>(Arrays.asList(8, 6, 4, 2, 1, 1));
        return points;
    }
    
    private Square position() {
        return this.board.getArena().getSquareByCoordinate(2, 0);
    }
    
    private List<Powerup> powerups() {
        List<Powerup> powerups = new ArrayList<>();
        powerups.add(new Powerup(TARGETING_SCOPE, AmmoType.BLUE));
        return powerups;
    }
    
    private Map<GameCharacter, Integer> revengeMarks() {
        Map<GameCharacter, Integer> marks = new HashMap<>();
        return marks;
    }

    private List<WeaponCard> weapons() {
        List<WeaponCard> weapons = new ArrayList<>();
        return weapons;
    }

    private AmmoTile squareTile() {
        Map<AmmoType, Integer> ammo = new HashMap<>();
        ammo.put(AmmoType.BLUE, 1);
        ammo.put(AmmoType.RED, 1);
        ammo.put(AmmoType.YELLOW, 0);
        return new AmmoTile(true, ammo);
    }

    private WeaponCard squareWeapon() {
        return new WeaponCard(Weapon.LOCK_RIFLE);
    }
    
    
    







}