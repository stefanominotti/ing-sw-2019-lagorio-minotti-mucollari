package it.polimi.se2019.controller;

import it.polimi.se2019.model.*;
import it.polimi.se2019.model.messages.board.ArenaMessage;
import it.polimi.se2019.model.messages.board.SkullsMessage;
import it.polimi.se2019.model.messages.client.*;
import it.polimi.se2019.model.messages.payment.PaymentMessage;
import it.polimi.se2019.model.messages.payment.PaymentSentMessage;
import it.polimi.se2019.model.messages.payment.PaymentType;
import it.polimi.se2019.model.messages.selections.SelectionListMessage;
import it.polimi.se2019.model.messages.selections.SelectionMessageType;
import it.polimi.se2019.model.messages.selections.SingleSelectionMessage;
import it.polimi.se2019.model.messages.turn.TurnMessage;
import it.polimi.se2019.model.messages.turn.TurnMessageType;
import it.polimi.se2019.server.ServerAllSender;
import it.polimi.se2019.server.ServerSingleSender;
import org.junit.Before;
import org.junit.Test;
import org.omg.PortableInterceptor.INACTIVE;

import java.util.*;

import static it.polimi.se2019.model.GameState.ENDED;
import static it.polimi.se2019.model.GameState.IN_GAME;
import static it.polimi.se2019.model.WeaponEffectOrderType.ALTERNATIVE;
import static it.polimi.se2019.model.WeaponEffectOrderType.PRIMARY;
import static it.polimi.se2019.model.messages.client.ClientMessageType.READY;
import static org.junit.Assert.*;

public class GameControllerTest {

    Board board;
    GameController controller;
    TurnController turnController;
    Player player;
    Player p1;
    Player p2;
    Player p3;

    @Before
    public void setUp() {
        this.board = new Board();
        this.controller = new GameController(this.board, new ServerSingleSender(null), new ServerAllSender(null));
        this.turnController = this.controller.getTurnController();
        this.board.addPlayer(GameCharacter.DOZER, "a", "123");
        this.controller.update(null, new ClientReadyMessage(GameCharacter.DOZER, "a", "123"));
        this.controller.update(null, new ClientReadyMessage(GameCharacter.SPROG, "b", "234"));
        this.controller.update(null, new ClientReadyMessage(GameCharacter.VIOLET, "c", "345"));
        this.controller.update(null, new ClientReadyMessage(GameCharacter.BANSHEE, "d", "456"));
        List<GameCharacter> characters = new ArrayList<>();
        characters.add(GameCharacter.DOZER);
        characters.add(GameCharacter.SPROG);
        characters.add(GameCharacter.VIOLET);
        characters.add(GameCharacter.BANSHEE);
        this.controller.update(null, new ClientReadyMessage(GameCharacter.BANSHEE, "d", "456"));
        this.controller.update(null, new SkullsMessage(4));
        this.controller.update(null, new ArenaMessage("4"));
        assertEquals(4, this.board.getSkulls());
        this.player = this.board.getPlayers().get(0);
        this.p1 = this.board.getPlayers().get(1);
        this.p2 = this.board.getPlayers().get(2);
        this.p3 = this.board.getPlayers().get(3);
        this.board.movePlayer(player, this.board.getArena().getSquareByCoordinate(1,0));
        this.board.movePlayer(p1, this.board.getArena().getSquareByCoordinate(0,0));
        this.board.movePlayer(p2, this.board.getArena().getSquareByCoordinate(2,0));
        this.controller.update(null, new TurnMessage(TurnMessageType.START, TurnType.FIRST_TURN, this.p3.getCharacter()));
        assertEquals(this.p3, this.turnController.getActivePlayer());
        this.board.movePlayer(p2, this.board.getArena().getSquareByCoordinate(0,0));
        this.turnController.setActivePlayer(this.player);
    }

    @Test
    public void reconnectionTest() {
        this.controller.update(null, new ClientDisconnectedMessage(this.player.getCharacter()));
        assertFalse(this.player.isConnected());
        assertEquals(1, this.board.getCurrentPlayer());
        this.controller.update(null, new ClientMessage(ClientMessageType.RECONNECTED,
                this.player.getCharacter()));
        assertTrue(this.player.isConnected());
    }

    @Test
    public void paymentTest() {
        //initialize
        this.board.movePlayer(this.player, this.board.getArena().getSquareByCoordinate(2,0));
        Powerup powerup = new Powerup(PowerupType.NEWTON, AmmoType.BLUE);
        this.player.addPowerup(powerup);
        Map<AmmoType, Integer> ammo = new HashMap<>();
        ammo.put(AmmoType.BLUE, 1);
        ammo.put(AmmoType.RED, 1);
        ammo.put(AmmoType.YELLOW, 1);
        WeaponCard weapon = this.player.getPosition().getWeaponsStore().get(0);
        this.turnController.setWeaponToGet(weapon);
        this.controller.update(null, new PaymentSentMessage(PaymentType.WEAPON,
                this.player.getCharacter(), ammo, new ArrayList<>(Arrays.asList(powerup))));
        assertEquals(0, this.player.getPowerups().size());
        assertEquals((Integer) 0, this.player.getAvailableAmmos().get(AmmoType.BLUE));
        assertEquals((Integer) 0, this.player.getAvailableAmmos().get(AmmoType.RED));
        assertEquals((Integer) 0, this.player.getAvailableAmmos().get(AmmoType.YELLOW));
        this.player.addWeapon(weapon);
        weapon.setReady(false);
        this.player.addPowerup(powerup);
        this.player.addAmmos(ammo);
        this.controller.update(null, new PaymentSentMessage(PaymentType.RELOAD,
                this.player.getCharacter(), ammo, new ArrayList<>(Arrays.asList(powerup))));
        assertEquals(0, this.player.getPowerups().size());
        assertEquals((Integer) 0, this.player.getAvailableAmmos().get(AmmoType.BLUE));
        assertEquals((Integer) 0, this.player.getAvailableAmmos().get(AmmoType.RED));
        assertEquals((Integer) 0, this.player.getAvailableAmmos().get(AmmoType.YELLOW));
        this.player.addPowerup(powerup);
        this.player.addAmmos(ammo);
        this.controller.update(null, new PaymentSentMessage(PaymentType.POWERUP,
                this.player.getCharacter(), ammo, new ArrayList<>(Arrays.asList(powerup))));
        assertEquals(0, this.player.getPowerups().size());
        assertEquals((Integer) 0, this.player.getAvailableAmmos().get(AmmoType.BLUE));
        assertEquals((Integer) 0, this.player.getAvailableAmmos().get(AmmoType.RED));
        assertEquals((Integer) 0, this.player.getAvailableAmmos().get(AmmoType.YELLOW));
    }

    @Test
    public void reloadTest() {
        Powerup powerup = new Powerup(PowerupType.NEWTON, AmmoType.BLUE);
        this.player.addPowerup(powerup);
        Map<AmmoType, Integer> ammo = new HashMap<>();
        ammo.put(AmmoType.BLUE, 1);
        ammo.put(AmmoType.RED, 1);
        ammo.put(AmmoType.YELLOW, 1);
        WeaponCard weapon = new WeaponCard(Weapon.MACHINE_GUN);
        this.player.addWeapon(weapon);
        weapon.setReady(false);
        this.controller.update(null, new SingleSelectionMessage(SelectionMessageType.RELOAD,
                this.player.getCharacter(), weapon.getWeaponType()));
        this.controller.update(null, new PaymentSentMessage(PaymentType.RELOAD,
                this.player.getCharacter(), ammo, new ArrayList<>(Arrays.asList(powerup))));
        assertEquals(0, this.player.getPowerups().size());
        assertEquals((Integer) 0, this.player.getAvailableAmmos().get(AmmoType.BLUE));
        assertEquals((Integer) 0, this.player.getAvailableAmmos().get(AmmoType.RED));
        assertEquals((Integer) 0, this.player.getAvailableAmmos().get(AmmoType.YELLOW));
    }

    @Test
    public void pickupSwitchWeaponTest() {
        this.board.movePlayer(this.player, this.board.getArena().getSquareByCoordinate(2,0));
        WeaponCard weapon = this.player.getPosition().getWeaponsStore().get(0);
        this.controller.update(null,
                new SingleSelectionMessage(SelectionMessageType.PICKUP_WEAPON, this.player.getCharacter(), weapon.getWeaponType()));
        assertEquals(weapon, this.turnController.getWeaponToGet());
        this.controller.update(null,
                new SingleSelectionMessage(SelectionMessageType.SWITCH, this.player.getCharacter(), weapon.getWeaponType()));
        assertEquals(weapon, this.turnController.getWeaponToGet());

    }

    @Test
    public void pickupMoveTest() {
        AmmoTile ammoTile = this.board.getArena().getSquareByCoordinate(0,0).getAvailableAmmoTile();
        Map<AmmoType, Integer> ammo = ammoTile.getAmmos();
        for (AmmoType ammoType : AmmoType.values()) {
            ammo.put(ammoType, ammo.get(ammoType) + this.player.getAvailableAmmos().get(ammoType));
        }
        this.controller.update(null, new SingleSelectionMessage(SelectionMessageType.PICKUP, this.player.getCharacter(),
                new Coordinates(0, 0)));
        if(ammoTile.hasPowerup()) {
            assertEquals(1, this.player.getPowerups().size());
        }
        for (AmmoType ammoType : AmmoType.values()) {
            assertEquals(ammo.get(ammoType), this.player.getAvailableAmmos().get(ammoType));
        }
    }

    @Test
    public void moveTest() {
        this.controller.update(null, new SingleSelectionMessage(SelectionMessageType.MOVE, this.player.getCharacter(),
                new Coordinates(0, 0)));
        assertEquals(this.board.getArena().getSquareByCoordinate(0,0), this.player.getPosition());
    }

    @Test
    public void usePowerupTest() {
        Powerup newton = new Powerup(PowerupType.NEWTON, AmmoType.BLUE);
        Powerup teleporter = new Powerup(PowerupType.TELEPORTER, AmmoType.BLUE);
        Powerup grenade = new Powerup(PowerupType.TAGBACK_GRENADE, AmmoType.BLUE);
        Powerup scope = new Powerup(PowerupType.TARGETING_SCOPE, AmmoType.BLUE);
        this.player.addPowerup(newton);
        this.player.addPowerup(teleporter);
        this.player.addPowerup(grenade);
        this.player.addPowerup(scope);
        this.controller.update(null,
                new SingleSelectionMessage(SelectionMessageType.USE_POWERUP, this.player.getCharacter(), null));

        this.controller.update(null,
                new SingleSelectionMessage(SelectionMessageType.USE_POWERUP, this.player.getCharacter(), newton));

        this.controller.update(null,
                new SingleSelectionMessage(SelectionMessageType.USE_POWERUP, this.player.getCharacter(), teleporter));
        this.controller.update(null,
                new SingleSelectionMessage(SelectionMessageType.POWERUP_POSITION, this.player.getCharacter(),
                        new Coordinates(0,0)));
        assertEquals(this.board.getArena().getSquareByCoordinate(0,0), this.player.getPosition());

        this.controller.setEffectTargets(new ArrayList<>(Arrays.asList(this.p1.getCharacter(), this.p2.getCharacter())));
        this.controller.update(null,
                new SingleSelectionMessage(SelectionMessageType.USE_POWERUP, this.player.getCharacter(), scope));
        this.controller.update(null, new SingleSelectionMessage(SelectionMessageType.POWERUP_TARGET, this.player.getCharacter(),
                p1.getCharacter()));
        assertEquals(1, this.p1.getDamages().size());

        this.controller.update(null,
                new SelectionListMessage<>(SelectionMessageType.USE_POWERUP,
                        this.player.getCharacter(), null));
        this.controller.update(null,
                new SelectionListMessage<>(SelectionMessageType.USE_POWERUP,
                        this.player.getCharacter(), new ArrayList<>(Arrays.asList(grenade))));

    }

    @Test
    public void payBackTest() {
        Powerup powerup = new Powerup(PowerupType.NEWTON, AmmoType.BLUE);
        Map<AmmoType, Integer> ammo = new HashMap<>();
        ammo.put(AmmoType.BLUE, 0);
        ammo.put(AmmoType.RED, 1);
        ammo.put(AmmoType.YELLOW, 1);
        this.controller.setPowerupsUsed(new ArrayList<>(Arrays.asList(powerup)));
        this.controller.setAmmoUsed(ammo);
        this.controller.payBack();
        assertEquals(new ArrayList<>(Arrays.asList(powerup)), this.player.getPowerups());
        assertEquals((Integer) 1, this.player.getAvailableAmmos().get(AmmoType.BLUE));
        assertEquals((Integer) 2, this.player.getAvailableAmmos().get(AmmoType.RED));
        assertEquals((Integer) 2, this.player.getAvailableAmmos().get(AmmoType.YELLOW));
    }

    @Test
    public void discardPowerupTest() {
        Powerup powerup = new Powerup(PowerupType.NEWTON, AmmoType.BLUE);
        this.player.addPowerup(powerup);
        this.controller.update(null, new SingleSelectionMessage(SelectionMessageType.DISCARD_POWERUP, this.player.getCharacter(), powerup));
        assertTrue(this.player.getPowerups().isEmpty());
    }



    /*@Test
    public void actionsTest() {
        this.controller.update(null, new SingleSelectionMessage(SelectionMessageType.ACTION, this.player.getCharacter(),
                ActionType.ENDTURN));
    }

    @Test
    public void weaponTest() {
        Weapon weapon = Weapon.TRACTOR_BEAM;
        this.player.addWeapon(new WeaponCard(weapon));
        this.controller.update(null,
                new SingleSelectionMessage(SelectionMessageType.USE_WEAPON, this.player.getCharacter(), weapon));
        this.controller.update(null,
                new SingleSelectionMessage(SelectionMessageType.EFFECT, this.player.getCharacter(), PRIMARY));
        this.controller.update(null,
                new SingleSelectionMessage(SelectionMessageType.EFFECT, this.player.getCharacter(), ALTERNATIVE));
        this.controller.update(null,
                new SingleSelectionMessage(SelectionMessageType.EFFECT, this.player.getCharacter(), null));
    }

    @Test
    public void askPowerupTest() {
        this.controller.update(null,
                new SingleSelectionMessage(SelectionMessageType.USE_WEAPON, this.player.getCharacter(), Weapon.MACHINE_GUN));
        List<GameCharacter> characters = new ArrayList<>(Arrays.asList(this.p1.getCharacter(), this.p2.getCharacter()));
        this.controller.askPowerup(characters);
        this.player.addPowerup(new Powerup(PowerupType.TARGETING_SCOPE, AmmoType.BLUE));
        this.controller.askPowerup(characters);
        this.p1.addPowerup(new Powerup(PowerupType.TAGBACK_GRENADE, AmmoType.RED));
        this.controller.askPowerup(characters);

    }*/

   @Test
    public void canPayPowerupTest() {
        this.player.addPowerup(new Powerup(PowerupType.TARGETING_SCOPE, AmmoType.BLUE));
        assertTrue(this.controller.canPayPowerup());
        Map<AmmoType, Integer> ammo = new HashMap<>();
        ammo.put(AmmoType.BLUE, 1);
        ammo.put(AmmoType.RED, 1);
        ammo.put(AmmoType.YELLOW, 1);
        this.board.useAmmos(this.player, ammo);
        assertFalse(this.controller.canPayPowerup());
    }

    @Test
    public void checkTagbackGrenadeCharacters() {
        this.p1.addPowerup(new Powerup(PowerupType.TAGBACK_GRENADE, AmmoType.RED));
        this.controller.setEffectTargets(new ArrayList<>(Arrays.asList(p1.getCharacter(), p2.getCharacter())));
        assertEquals(new ArrayList<>(Arrays.asList(p1.getCharacter())), this.controller.checkTagbackGrenadeCharacters());
    }

    @Test
    public void notPersistence() {
        this.controller.update(null, new SingleSelectionMessage(SelectionMessageType.PERSISTENCE, this.player.getCharacter(), "n"));
        assertEquals(ENDED, this.board.getGameState());
    }

    @Test
    public void activePlayerDisconnected() {
        this.controller.update(null, new ClientDisconnectedMessage(this.player.getCharacter()));
        assertFalse(this.player.isConnected());
    }

}