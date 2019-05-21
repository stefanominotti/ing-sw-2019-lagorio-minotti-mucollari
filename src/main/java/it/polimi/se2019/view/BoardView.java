package it.polimi.se2019.view;

import it.polimi.se2019.model.*;

import java.util.*;

public class BoardView {

    private int skulls;
    private List<SquareView> squares;
    private List<GameCharacter> killshotTrack;
    private Map<GameCharacter, SquareView> positions;

    BoardView(int skulls, List<SquareView> squares) {
        this.killshotTrack = new ArrayList<>();
        this.skulls = skulls;
        this.squares = squares;
        this.positions = new EnumMap<>(GameCharacter.class);
    }

    BoardView(int skulls, List<SquareView> squares,
              List<GameCharacter> killshotTrack) {
        this.skulls = skulls;
        this.squares = squares;
        this.killshotTrack = killshotTrack;
        this.positions = new EnumMap<>(GameCharacter.class);
        for(SquareView square : this.squares) {
            for(GameCharacter player : square.getActivePlayers()) {
                this.positions.put(player, square);
            }
        }
    }

    public int getSkulls() {
        return this.skulls;
    }

    public List<SquareView> getSquares() {
        return new ArrayList<>(this.squares);
    }

    public SquareView getSquareByCoordinates(int x, int y) {
        for(SquareView square : this.squares) {
            if(square.getX() == x && square.getY() == y) {
                return square;
            }
        }
        throw new IllegalStateException("No square with given coordinates");
    }

    public void setPlayerPosition(GameCharacter player, SquareView square) {
        if (this.positions.get(player) != null) {
            this.positions.get(player).removeActivePlayer(player);
        }
        this.positions.put(player, square);
        if (square != null) {
            square.addActivePlayer(player);
        }
    }

    public SquareView getPlayerPosition(GameCharacter character) {
        return this.positions.get(character);
    }

    public List<GameCharacter> getKillshotTrack() {
        return new ArrayList<>(this.killshotTrack);
    }

    void addKillshotPoints(GameCharacter attacker, int amount) {}

    void decrementSkulls(int amount) {
        this.skulls -= amount;
    }
}
