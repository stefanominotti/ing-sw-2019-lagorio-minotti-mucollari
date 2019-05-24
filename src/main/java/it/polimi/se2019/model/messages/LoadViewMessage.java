package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.Powerup;
import it.polimi.se2019.model.Weapon;
import it.polimi.se2019.view.PlayerBoard;
import it.polimi.se2019.view.SquareView;

import java.util.List;
import java.util.Map;

public class LoadViewMessage extends Message implements SingleReceiverMessage {

    private final GameCharacter character;
    private final String nickname;
    private final int skulls;
    private final List<SquareView> squares;
    private final Map<Integer, List<GameCharacter>> killshotTrack;
    private final List<PlayerBoard> payerBoards;
    //self
    private final List<Weapon> readyWeapons;
    private final List<Powerup> powerups;
    private final int score;
    private final Map<GameCharacter, String> otherPlayers;

    public LoadViewMessage(GameCharacter character, String nickname, int skulls, List<SquareView> squares,
                           Map<Integer, List<GameCharacter>> killshotTrack, List<PlayerBoard> playerBoards,
                           List<Weapon> readyWeapons, List<Powerup> powerups, int score,
                           Map<GameCharacter, String> otherPlayers) {

        setMessageType(this.getClass());
        this.character = character;
        this.nickname = nickname;
        this.skulls = skulls;
        this.squares = squares;
        this.killshotTrack = killshotTrack;
        this.payerBoards = playerBoards;
        this.readyWeapons = readyWeapons;
        this.powerups = powerups;
        this.score = score;
        this.otherPlayers = otherPlayers;
    }

    public GameCharacter getCharacter() {
        return this.character;
    }

    public List<SquareView> getSquares() {
        return this.squares;
    }

    public Map<Integer, List<GameCharacter>> getKillshotTrack() {
        return this.killshotTrack;
    }

    public int getSkulls() {
        return this.skulls;
    }

    public List<PlayerBoard> getPayerBoards() {
        return this.payerBoards;
    }

    public List<Weapon> getReadyWeapons() {
        return this.readyWeapons;
    }

    public List<Powerup> getPowerups() {
        return this.powerups;
    }

    public int getScore() {
        return this.score;
    }

    public String getNickname() {
        return  this.nickname;
    }

    public Map<GameCharacter, String> getOtherPlayers() {
        return this.otherPlayers;
    }
}
