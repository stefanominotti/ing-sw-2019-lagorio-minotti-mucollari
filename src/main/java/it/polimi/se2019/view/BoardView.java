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
        for (SquareView square : this.squares) {
            square.setBoard(this);
        }
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
            square.setBoard(this);
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
        return null;
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

    public String arenaToString() {
        StringBuilder builder = new StringBuilder();
        for (int i=0; i<3; i++) {
            String row;
            List<String[]> squares = new ArrayList<>();
            for (int j=0; j<4; j++) {
                SquareView square = getSquareByCoordinates(j, i);
                if (square == null) {
                    squares.add(emptySquare().split("\n"));
                } else {
                    squares.add(square.toString().split("\n"));
                }
            }
            for(int j=0; j<10; j++) {
                String line = squares.get(0)[j] + squares.get(1)[j] + squares.get(2)[j] + squares.get(3)[j];
                builder.append(line + "\n");
            }
        }

        return builder.toString();
    }

    public String emptySquare() {
        StringBuilder builder = new StringBuilder();
        for (int i=0; i<23; i++) {
            builder.append("                       \n");
        }
        return builder.toString();
    }
}
