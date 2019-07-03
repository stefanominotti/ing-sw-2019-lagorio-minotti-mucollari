package it.polimi.se2019.model.messages.client;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.playerassets.Powerup;
import it.polimi.se2019.model.playerassets.weapon.Weapon;
import it.polimi.se2019.view.PlayerBoard;
import it.polimi.se2019.view.SquareView;

import java.util.List;
import java.util.Map;

/**
 * Class for handling load view message
 */
public class LoadViewMessage extends ClientMessage {

    private final String nickname;
    private final int skulls;
    private final List<SquareView> squares;
    private final Map<Integer, List<GameCharacter>> killshotTrack;
    private final List<PlayerBoard> playerBoards;
    //self
    private final List<Weapon> readyWeapons;
    private final List<Powerup> powerups;
    private final int score;
    private final Map<GameCharacter, String> otherPlayers;
    private boolean frenzy;
    private boolean beforeFirstPlayer;
    private int arena;
    private List<GameCharacter> deadPlayers;

    /**
     * Class constructor, it builds a load view message
     * @param character addressee of the message
     * @param nickname of the addressee of the message
     * @param skulls number set for the game
     * @param squares of the game
     * @param killshotTrack of your damages
     * @param playerBoards of the other player
     * @param readyWeapons of the addressee
     * @param powerups of the addressee
     * @param score of the addressee
     * @param otherPlayers map with the other players nickname-character
     * @param frenzy true if the Final Frenzy mode is activated, else false
     * @param beforeFirstPlayer true if the addressee plays before the first player in the current turn, else false
     * @param arena integer representing arena number
     * @param deadPlayers List of dead players
     */
    public LoadViewMessage(GameCharacter character, String nickname, int skulls, List<SquareView> squares,
                           Map<Integer, List<GameCharacter>> killshotTrack, List<PlayerBoard> playerBoards,
                           List<Weapon> readyWeapons, List<Powerup> powerups, int score,
                           Map<GameCharacter, String> otherPlayers, boolean frenzy, boolean beforeFirstPlayer,
                           int arena, List<GameCharacter> deadPlayers) {

        super(ClientMessageType.LOAD_VIEW, character);
        this.nickname = nickname;
        this.skulls = skulls;
        this.squares = squares;
        this.killshotTrack = killshotTrack;
        this.playerBoards = playerBoards;
        this.readyWeapons = readyWeapons;
        this.powerups = powerups;
        this.score = score;
        this.otherPlayers = otherPlayers;
        this.beforeFirstPlayer = frenzy;
        this.frenzy = beforeFirstPlayer;
        this.arena = arena;
        this.deadPlayers = deadPlayers;
    }

    /**
     * Gets the square view to show the arena
     * @return List of square view
     */
    public List<SquareView> getSquares() {
        return this.squares;
    }

    /**
     * Gets the kill shot track for the addressee
     * @return Map with game characters and the corresponding dealt damages to the addressee
     */
    public Map<Integer, List<GameCharacter>> getKillshotTrack() {
        return this.killshotTrack;
    }

    /**
     * Gets skulls number for the game
     * @return the current number of the skulls
     */
    public int getSkulls() {
        return this.skulls;
    }

    /**
     * Gets the other players board
     * @return List of the other players board
     */
    public List<PlayerBoard> getPlayerBoards() {
        return this.playerBoards;
    }

    /**
     * Gets the ready weapons of the addressee
     * @return List of the ready weapons of the addressee
     */
    public List<Weapon> getReadyWeapons() {
        return this.readyWeapons;
    }

    /**
     * Gets the available power ups of the addressee
     * @return List of the available power ups of the addressee
     */
    public List<Powerup> getPowerups() {
        return this.powerups;
    }

    /**
     * Gets the current score of the addressee
     * @return the current score raised by the addressee
     */
    public int getScore() {
        return this.score;
    }

    /**
     * Gets the nickname of the addressee
     * @return the nickname of the addressee
     */
    public String getNickname() {
        return  this.nickname;
    }

    /**
     * Gets the other players nickname-character
     * @return Map with the game character and his corresponding nickname
     */
    public Map<GameCharacter, String> getOtherPlayers() {
        return this.otherPlayers;
    }

    /**
     * Knows if the Final Frenzy mode is active
     * @return true if it is, else false
     */
    public boolean isFrenzy() {
        return this.frenzy;
    }


    /**
     * Knows if the addressee plays before the first player in the current turn
     * @return true if it is, else false
     */
    public boolean isBeforeFirstPlayer() {
        return this.beforeFirstPlayer;
    }

    /**
     * Gets the arena number
     * @return arena number
     */
    public int getArena() {
        return this.arena;
    }


    /**
     * Gets dead players list
     * @return List of dead players
     */
    public List<GameCharacter> getDeadPlayers() {
        return this.deadPlayers;
    }
}