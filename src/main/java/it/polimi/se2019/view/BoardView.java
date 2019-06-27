package it.polimi.se2019.view;

import it.polimi.se2019.model.*;

import java.util.*;

import static it.polimi.se2019.view.SquareStringUtils.*;

public class BoardView {

    private int skulls;
    private List<SquareView> squares;
    private Map<Integer, List<GameCharacter>> killshotTrack;
    private Map<GameCharacter, SquareView> positions;
    private boolean frenzy;
    private boolean beforeFirstPlayer;

    BoardView(int skulls, List<SquareView> squares) {
        this.killshotTrack = new HashMap<>();
        this.skulls = skulls;
        this.squares = squares;
        this.beforeFirstPlayer = false;
        this.frenzy = false;
        this.positions = new EnumMap<>(GameCharacter.class);
        for (SquareView square : this.squares) {
            square.setBoard(this);
        }
    }

    BoardView(int skulls, List<SquareView> squares,
              Map<Integer, List<GameCharacter>> killshotTrack, boolean frenzy, boolean beforeFirstPlayer) {
        this.skulls = skulls;
        this.squares = squares;
        this.killshotTrack = killshotTrack;
        this.frenzy = frenzy;
        this.beforeFirstPlayer = beforeFirstPlayer;
        this.positions = new EnumMap<>(GameCharacter.class);
        for(SquareView square : this.squares) {
            for(GameCharacter player : square.getActivePlayers()) {
                this.positions.put(player, square);
            }
            square.setBoard(this);
        }
    }

    void setBeforeFirstPlayer(boolean beforeFirstPlayer) {
        this.beforeFirstPlayer = beforeFirstPlayer;
    }

    boolean isBeforeFirstPlayer() {
        return this.beforeFirstPlayer;
    }

    void startFrenzy() {
        this.frenzy = true;
    }

    boolean isFrenzy() {
        return this.frenzy;
    }

    public int getSkulls() {
        return this.skulls;
    }

    void setSkulls(int skulls) {
        this.skulls = skulls;
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

    public Map<Integer, List<GameCharacter>> getKillshotTrack() {
        return new HashMap<>(this.killshotTrack);
    }

    void addKillshotPoints(List<GameCharacter> players, int skullNumber) {
        this.killshotTrack.put(skullNumber, players);
    }


    String arenaToString() {
        return arenaToString(new ArrayList<>());
    }

    String arenaToString(List<Coordinates> markedCoordinates) {

        StringBuilder builder = new StringBuilder();

        builder.append("  ");
        for(int i=0; i<4; i++) {
            builder.append(center(String.valueOf(i), 23));
        }
        builder.append("\n");

        for (int i=0; i<3; i++) {
            String row;
            List<List<String>> squares = new ArrayList<>();
            for (int j = 0; j < 4; j++) {
                boolean marked = false;
                SquareView square = getSquareByCoordinates(j, i);
                if (square == null) {
                    squares.add(Arrays.asList(emptySquare().split("\n")));
                } else {
                    for (Coordinates c : markedCoordinates) {
                        if (c.getX() == j && c.getY() == i) {
                            marked = true;
                            break;
                        }
                    }
                    if (marked) {
                        squares.add(Arrays.asList(square.toString(true).split("\n")));
                    } else {
                        squares.add(Arrays.asList(square.toString(false).split("\n")));
                    }
                }
            }
            if (i == 1) {
                squares.add(Arrays.asList(legendSquare(new ArrayList<>(this.positions.keySet())).split("\n")));
            }
            for (int j = 0; j < 10; j++) {
                int count = 0;
                for (List<String> square : squares) {
                    if (count == 0 && j == 4) {
                        builder.append(i + " ");
                    } else if (count == 0) {
                        builder.append("  ");
                    }
                    builder.append(square.get(j));
                    count++;
                }
                builder.append("\n");
            }
        }

        return builder.toString();
    }

    String killshotTrackToString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Skulls left: " + this.skulls + "\n");
        builder.append("Kill points: ");
        for (Map.Entry<Integer, List<GameCharacter>> kill : this.killshotTrack.entrySet()) {
            int i = 0;
            for (GameCharacter c : kill.getValue()) {
                builder.append(c.getIdentifier() + " ");
                i++;
            }
            while (i < 2) {
                builder.append("_ ");
                i++;
            }
            builder.append("|");
        }
        builder.setLength(builder.length() - 1);
        builder.append("\n");
        return builder.toString();
    }
}
