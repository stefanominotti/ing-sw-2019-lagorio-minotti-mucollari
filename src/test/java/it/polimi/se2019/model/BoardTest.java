package it.polimi.se2019.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;


public class BoardTest {
    Board board ;

    @Before
    public void setUp(){
        this.board = new Board();
    }

    @Test
    public void notNullTest() {
        assertNotNull(this.board);
    }

    @Test
    public void getPlayersTest() {
        this.board.addPlayer(GameCharacter.BANSHEE);
        assertNotNull(this.board.getPlayers());
        assertEquals(1 , this.board.getPlayers().size());
    }

    @Test
    public void getValidPlayersTest() {
        this.board.addPlayer(GameCharacter.BANSHEE);
        assertNotNull(this.board.getPlayers());
        assertEquals(1 , this.board.getPlayers().size());
    }

    @Test
    public void getValidCharactersTest() {
        this.board.addPlayer(GameCharacter.BANSHEE);
        this.board.setPlayerNickname(GameCharacter.BANSHEE, "testNickname");
        assertNotNull(this.board.getValidCharacters());
        assertEquals(Arrays.asList(GameCharacter.BANSHEE), this.board.getValidCharacters());
    }

    @Test
    public void getPlayerByCharacter() {
        this.board.addPlayer(GameCharacter.BANSHEE);
        assertNotNull(this.board.getValidCharacters());
        assertEquals(this.board.getPlayers().get(0), this.board.getPlayerByCharacter(GameCharacter.BANSHEE));
    }

    @Test
    public void skullTest() {
        this.board.addPlayer(GameCharacter.BANSHEE);
        this.board.setPlayerNickname(GameCharacter.BANSHEE, "testNickname");
        this.board.setSkulls(5);
        assertEquals(5, this.board.getSkulls());
    }


}
