package it.polimi.se2019.controller;

import it.polimi.se2019.model.*;
import it.polimi.se2019.model.messages.board.ArenaMessage;
import it.polimi.se2019.model.messages.board.SkullsMessage;
import it.polimi.se2019.model.messages.client.*;
import it.polimi.se2019.model.messages.selections.SelectionMessageType;
import it.polimi.se2019.model.messages.selections.SingleSelectionMessage;
import it.polimi.se2019.model.messages.turn.TurnMessage;
import it.polimi.se2019.model.messages.turn.TurnMessageType;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static it.polimi.se2019.model.GameState.ENDED;
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
        this.controller = new GameController(this.board, null);
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
        int index = 0;
        for(GameCharacter character : characters) {
            assertEquals(character, this.board.getPlayers().get(index).getCharacter());
            index++;
        }
        this.controller.update(null, new ClientDisconnectedMessage(GameCharacter.BANSHEE));
        characters.remove(GameCharacter.BANSHEE);
        index = 0;
        for(GameCharacter character : characters) {
            assertEquals(character, this.board.getPlayers().get(index).getCharacter());
            index++;
        }
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
        this.board.movePlayer(p3, this.board.getArena().getSquareByCoordinate(1,1));
        this.turnController.setActivePlayer(this.player);
    }


    @Test
    public void paymentTest() {
        Powerup powerup = new Powerup(PowerupType.NEWTON, AmmoType.BLUE);
        this.player.addPowerup(powerup);
        Map<AmmoType, Integer> ammo = new HashMap<>();
        ammo.put(AmmoType.RED, 1);
        this.controller.payment(ammo, new ArrayList<>(Arrays.asList(powerup)));
        assertEquals((Integer) 0, this.player.getAvailableAmmos().get(AmmoType.RED));
        assertTrue(this.player.getPowerups().isEmpty());
    }

    @Test
    public void discardPowerupTest() {
        Powerup powerup = new Powerup(PowerupType.NEWTON, AmmoType.BLUE);
        this.player.addPowerup(powerup);
        this.controller.update(null, new SingleSelectionMessage(SelectionMessageType.DISCARD_POWERUP, this.player.getCharacter(), powerup));
        assertTrue(this.player.getPowerups().isEmpty());
    }

    @Test
    public void handlePowerupRequestTest() {
        this.controller.setPowerupRequests(2);
        assertEquals(2, this.controller.getPowerupRequests());
        this.controller.handlePowerupRequests();
        assertEquals(1, this.controller.getPowerupRequests());

    }

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
    public void notPersistence() {
        this.controller.update(null, new SingleSelectionMessage(SelectionMessageType.PERSISTENCE, this.player.getCharacter(), "n"));
        assertEquals(ENDED, this.board.getGameState());
    }
}