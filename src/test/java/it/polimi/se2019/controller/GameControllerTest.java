package it.polimi.se2019.controller;

import it.polimi.se2019.model.*;
import it.polimi.se2019.model.messages.board.ArenaMessage;
import it.polimi.se2019.model.messages.board.SkullsMessage;
import it.polimi.se2019.model.messages.client.*;
import it.polimi.se2019.model.messages.turn.TurnMessage;
import it.polimi.se2019.model.messages.turn.TurnMessageType;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.se2019.model.messages.client.ClientMessageType.READY;
import static org.junit.Assert.*;

public class GameControllerTest {

    Board board;
    GameController controller;
    Player player;
    Player p1;
    Player p2;
    Player p3;

    @Before
    public void setUp() {
        this.board = new Board();
        this.controller = new GameController(this.board, null);
    }

    @Test
    public void clientConnectionTest() {
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
    }
}