package it.polimi.se2019.view;

import it.polimi.se2019.model.GameCharacter;

import java.util.ArrayList;
import java.util.List;

public class ResetPlayerBoardMessage {

    private GameCharacter player;
    private List<Integer> killshotPoints;

    public ResetPlayerBoardMessage(GameCharacter player, List<Integer> killshotPoints) {
        this.player = player;
        this.killshotPoints = killshotPoints;
    }

    public GameCharacter getPlayer() {
        return this.player;
    }

    public List<Integer> getKillshotPoints() {
        return new ArrayList<>(this.killshotPoints);
    }
}
