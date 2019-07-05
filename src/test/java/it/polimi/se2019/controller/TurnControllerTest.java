package it.polimi.se2019.controller;

import it.polimi.se2019.model.*;
import it.polimi.se2019.model.arena.Coordinates;
import it.polimi.se2019.model.arena.RoomColor;
import it.polimi.se2019.model.playerassets.AmmoType;
import it.polimi.se2019.model.playerassets.Powerup;
import it.polimi.se2019.model.playerassets.PowerupType;
import it.polimi.se2019.model.playerassets.weapons.EffectType;
import it.polimi.se2019.model.playerassets.weapons.Weapon;
import it.polimi.se2019.model.playerassets.weapons.WeaponCard;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class TurnControllerTest {

    Board board;
    Player player;
    Player p1;
    Player p2;
    GameController gameController;
    TurnController controller;
    EffectsController effectsController;

    @Before
    public void setUp() {
        this.board = new Board();
        this.board.createArena("4");
        this.gameController = new GameController(this.board, null);
        this.effectsController = new EffectsController(this.board, this.gameController);
        this.controller = new TurnController(this.board, this.gameController,this.effectsController );
        this.board.addPlayer(GameCharacter.DOZER, "a", "123");
        this.board.addPlayer(GameCharacter.SPROG, "b", "234");
        this.board.addPlayer(GameCharacter.VIOLET, "c", "345");
        this.player = this.board.getPlayers().get(0);
        this.p1 = this.board.getPlayers().get(1);
        this.p2 = this.board.getPlayers().get(2);
        this.board.movePlayer(this.player, this.board.getArena().getSquareByCoordinate(1, 0));
        this.board.movePlayer(p1, this.board.getArena().getSquareByCoordinate(2, 0));
        this.board.movePlayer(p2, this.board.getArena().getSquareByCoordinate(1, 1));
        this.controller.setActivePlayer(player);
        this.board.setSkulls(2);
        this.board.finalizeGameSetup();
        assertEquals(this.player, this.controller.getActivePlayer());
        this.controller.startTurn(TurnType.NORMAL, this.player.getCharacter());
    }

    @Test
    public void startTurnMovesLeftTest() {
        this.controller.startTurn(TurnType.FIRST_TURN, this.player.getCharacter());
        assertEquals(2, this.player.getPowerups().size());
        assertEquals(2, this.controller.getMovesLeft());
        this.controller.startTurn(TurnType.AFTER_DEATH, this.player.getCharacter());
        assertEquals(3, this.player.getPowerups().size());
        assertEquals(0, this.controller.getMovesLeft());
        this.controller.startTurn(TurnType.NORMAL, this.player.getCharacter());
        assertEquals(2, this.controller.getMovesLeft());
        this.controller.startTurn(TurnType.FINAL_FRENZY_FIRST, this.player.getCharacter());
        assertEquals(2, this.controller.getMovesLeft());
        this.controller.startTurn(TurnType.FINAL_FRENZY_AFTER, this.player.getCharacter());
        assertEquals(1, this.controller.getMovesLeft());
    }

    @Test
    public void handlePowerupDiscardTest() {
        this.controller.setState(TurnState.DEATH_RESPAWNING);
        this.board.drawPowerup(this.player);
        RoomColor color = RoomColor.valueOf(this.player.getPowerups().get(0).getColor().toString());
        this.controller.handlePowerupDiscarded(this.player.getPowerups().get(0));
        assertTrue(this.player.getPowerups().isEmpty());
        assertEquals(color, this.player.getPosition().getRoom().getColor());

    }

    @Test
    public void calculateMovmentTest() {
        List<Coordinates> coordinates = new ArrayList<>();
        coordinates.add(new Coordinates(0,0));
        coordinates.add(new Coordinates(0,1));
        coordinates.add(new Coordinates(0,2));
        coordinates.add(new Coordinates(1,1));
        coordinates.add(new Coordinates(1,2));
        coordinates.add(new Coordinates(2,0));
        coordinates.add(new Coordinates(2,1));
        coordinates.add(new Coordinates(2,2));
        coordinates.add(new Coordinates(3,1));
        assertTrue(assertCoordinates(coordinates, this.controller.calculateMovementAction()));
        this.controller.activeFinalFrenzy();
        coordinates.add(new Coordinates(3,2));
        assertTrue(assertCoordinates(coordinates, this.controller.calculateMovementAction()));
    }

    @Test
    public void calculatePickupTest() {
        List<Coordinates> coordinates = new ArrayList<>();
        coordinates.add(new Coordinates(0,0));
        coordinates.add(new Coordinates(2,0));
        coordinates.add(new Coordinates(1,1));
        coordinates.add(new Coordinates(1,0));
        assertTrue(assertCoordinates(coordinates, this.controller.calculatePickupAction()));
        this.board.attackPlayer(this.p1.getCharacter(), this.player.getCharacter(),4, EffectType.DAMAGE);
        coordinates.add(new Coordinates(1,2));
        coordinates.add(new Coordinates(2,1));
        coordinates.add(new Coordinates(0,1));
        assertTrue(assertCoordinates(coordinates, this.controller.calculatePickupAction()));
        this.controller.activeFinalFrenzy();
        coordinates.add(new Coordinates(0,2));
        coordinates.add(new Coordinates(3,1));
        coordinates.add(new Coordinates(2,2));
        assertTrue(assertCoordinates(coordinates, this.controller.calculatePickupAction()));
    }

    @Test
    public void moveReloadShootMovementsTest() {
        this.player.addWeapon(new WeaponCard(Weapon.MACHINE_GUN));
        this.controller.activeFinalFrenzy();
        List<Coordinates> coordinates = new ArrayList<>();
        coordinates.add(new Coordinates(0,0));
        coordinates.add(new Coordinates(1,0));
        coordinates.add(new Coordinates(1,1));
        coordinates.add(new Coordinates(1,2));
        coordinates.add(new Coordinates(2,0));
        coordinates.add(new Coordinates(2,1));
        assertTrue(assertCoordinates(coordinates, this.controller.getMoveReloadShootMovements()));
    }

    @Test
    public void calculateShootActionFrenzyTest() {
        this.controller.setFinalFrenzy(true);
        this.controller.handleAction(ActionType.SHOOT);
        assertTrue(this.controller.getMoveShoot());
    }

    @Test
    public void handleEndActionsTest() {
        this.controller.handleEndAction();
        assertEquals(TurnState.SELECTACTION, this.controller.getState());
    }

    @Test
    public void calculatePowerupActionTest() {
        List<Powerup> powerups = new ArrayList<>();
        Powerup newton = new Powerup(PowerupType.NEWTON, AmmoType.BLUE);
        Powerup teleport = new Powerup(PowerupType.TELEPORTER, AmmoType.RED);
        this.player.addPowerup(newton);
        this.player.addPowerup(teleport);
        powerups.add(newton);
        powerups.add(teleport);
        assertEquals(powerups, this.controller.calculatePowerupAction());
    }

    @Test
    public void calculateActionsTest() {
        List<ActionType> availableActions = new ArrayList<>(Arrays.asList(ActionType.MOVE, ActionType.PICKUP));
        this.player.addWeapon(new WeaponCard(Weapon.MACHINE_GUN));
        this.player.addPowerup(new Powerup(PowerupType.NEWTON, AmmoType.BLUE));
        availableActions.add(ActionType.SHOOT);
        availableActions.add(ActionType.POWERUP);
        assertEquals(availableActions, this.controller.calculateActions());
    }

    @Test
    public void canUseNewtonTest() {
        this.player.addPowerup(new Powerup(PowerupType.NEWTON, AmmoType.BLUE));
        this.p1.setPosition(this.board.getArena().getSquareByCoordinate(1,0));
        assertTrue(this.controller.canUseNewton());
        this.p1.setPosition(this.board.getArena().getSquareByCoordinate(2,0));
        assertTrue(this.controller.canUseNewton());
        this.p1.setDead(true);
        assertTrue(this.controller.canUseNewton());
        this.p2.setPosition(this.board.getArena().getSquareByCoordinate(1,2));
        assertTrue(this.controller.canUseNewton());
    }

    @Test
    public void canReloadTest() {
        this.player.addWeapon(new WeaponCard(Weapon.MACHINE_GUN));
        assertFalse(this.controller.canReload());
        this.player.getWeapons().get(0).setReady(false);
        assertTrue(this.controller.canReload());
        this.controller.setWeaponToGet(this.player.getWeapons().get(0));
        this.controller.reloadWeapon();
        assertTrue(this.player.getWeapons().get(0).isReady());
    }

    @Test
    public void handleMovementActionTest() {;
        this.controller.handleMovementAction(new Coordinates(0,0));
        assertEquals(this.board.getArena().getSquareByCoordinate(0,0), this.player.getPosition());
    }

    @Test
    public void handlePickupActionTest() {
        assertNull(this.controller.handlePickupAction(new Coordinates(0,0)));
        assertNotNull(this.controller.handlePickupAction(new Coordinates(0,1)));
    }

    @Test
    public void handlePaidWeaponTest() {
        this.board.movePlayer(this.player, this.board.getArena().getSquareByCoordinate(2,0));
        WeaponCard weapon = this.player.getPosition().getWeaponsStore().get(0);
        this.controller.setWeaponToGet(weapon);
        this.controller.handlePaidWeapon();
        assertEquals(weapon, this.player.getWeapons().get(0));
    }

    @Test
    public void handleSwitchWeaponTest() {
        this.player.addWeapon(new WeaponCard(Weapon.MACHINE_GUN));
        this.controller.setWeaponToGet(new WeaponCard(Weapon.LOCK_RIFLE));
        assertFalse(this.controller.handleSwitchWeapon(Weapon.MACHINE_GUN));
    }

    @Test
    public void cancelTest() {
        this.controller.setMovesLeft(1);
        this.controller.setMoveShoot(true);
        this.controller.cancelAction();
        assertEquals(2, this.controller.getMovesLeft());
    }

    @Test
    public void endTurn() {
        this.controller.handleAction(ActionType.ENDTURN);
        assertEquals(1, this.board.getCurrentPlayer());
        this.controller.handleAction(ActionType.RELOAD);
        assertEquals(2, this.board.getCurrentPlayer());
        this.controller.setMoveShoot(true);
        this.controller.handleAction(ActionType.RELOAD);
        assertEquals(2, this.board.getCurrentPlayer());
        assertEquals(TurnState.SELECTACTION, this.controller.getState());

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

}