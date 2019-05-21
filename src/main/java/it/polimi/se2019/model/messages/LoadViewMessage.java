package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.Powerup;
import it.polimi.se2019.model.Weapon;
import it.polimi.se2019.view.PlayerBoard;
import it.polimi.se2019.view.SquareView;

import java.util.List;

public class LoadViewMessage extends Message implements SingleReceiverMessage {

    private final GameCharacter character;
    private final String nickname;
    private final int skulls;
    private final List<SquareView> squares;
    private final List<GameCharacter> killshotTrack;
    private final List<PlayerBoard> payerBoards;
    //self
    private final List<Weapon> readyWeapons;
    private final List<Powerup> powerups;
    private final int score;

    public LoadViewMessage(GameCharacter character, String nickname, int skulls, List<SquareView> squares,
                           List<GameCharacter> killshotTrack, List<PlayerBoard> playerBoards,
                           List<Weapon> readyWeapons, List<Powerup> powerups, int score) {

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
    }

    public GameCharacter getCharacter() {
        return this.character;
    }

    public List<SquareView> getSquares() {
        return this.squares;
    }

    public List<GameCharacter> getKillshotTrack() {
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
}
