package it.polimi.se2019.controller;

import it.polimi.se2019.model.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class PowerupsControllerTest {

    Board board;
    Player player;
    Player p1;
    Player p2;
    GameController gameController;
    TurnController turnController;
    PowerupsController controller;

    @Before
    public void setUp(){
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
        this.gameController = new GameController(this.board, null);
        this.turnController = new TurnController(this.board, this.gameController, null);
        this.controller = new PowerupsController(this.board, this.gameController, this.turnController);
        this.controller.setActivePlayer(this.player);
        List<GameCharacter> characters = new ArrayList<>();
        characters.add(p1.getCharacter());
        characters.add(p2.getCharacter());
        this.gameController.setEffectTargets(characters);
        this.player.addPowerup(new Powerup(PowerupType.NEWTON, null));
        this.player.addPowerup(new Powerup(PowerupType.TELEPORTER, null));
        this.player.addPowerup(new Powerup(PowerupType.TARGETING_SCOPE, null));
    }

    @Test
    public void avialableTargetsTest() {
        List<GameCharacter> characters;
        characters = new ArrayList<>();
        characters.add(p1.getCharacter());
        characters.add(p2.getCharacter());
        this.controller.setActivePowerup(PowerupType.TARGETING_SCOPE);
        assertEquals(characters, this.controller.avialableTargets());
        this.controller.setActivePowerup(PowerupType.NEWTON);
        assertEquals(characters, this.controller.avialableTargets());
    }

    //case newton ewst south
    @Test
    public void avialablePositionsTest() {
        this.controller.setTarget(p1);
        List<Coordinates> coordinates;
        coordinates = new ArrayList<>();
        coordinates.add(new Coordinates(1,0));
        coordinates.add(new Coordinates(2,1));
        coordinates.add(new Coordinates(0,0));
        this.controller.setActivePowerup(PowerupType.NEWTON);
        assertTrue(assertCoordinates(coordinates, this.controller.avialablePositions()));
        coordinates = new ArrayList<>();
        for (Square square : this.board.getArena().getAllSquares()) {
            coordinates.add(new Coordinates(square.getX(), square.getY()));
        }
        this.controller.setActivePowerup(PowerupType.TELEPORTER);
        assertTrue(assertCoordinates(coordinates, this.controller.avialablePositions()));
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