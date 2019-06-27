package it.polimi.se2019.view;

import it.polimi.se2019.model.*;

import java.util.*;

import static it.polimi.se2019.view.SquareStringUtils.*;

/**
 * Class for handling board view
 */
public class BoardView {

    private int skulls;
    private List<SquareView> squares;
    private Map<Integer, List<GameCharacter>> killshotTrack;
    private Map<GameCharacter, SquareView> positions;
    private boolean frenzy;
    private boolean beforeFirstPlayer;

    /**
     * Class constructor, it builds a board view
     * @param skulls number set for the board
     * @param squares square view which compose the arena
     */
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

    /**
     * Class constructor, it builds a board view when a game is already started and you want to resume it
     * @param skulls number set for the board
     * @param squares square view which compose the arena
     * @param killshotTrack killshot tracks of the game characters
     * @param frenzy true if Final Frenzy is active, else false
     * @param beforeFirstPlayer true if the current player is playing before the first player, else false
     */
    BoardView(int skulls, List<SquareView> squares, Map<Integer, List<GameCharacter>> killshotTrack, boolean frenzy,
              boolean beforeFirstPlayer) {

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

    /**
     * Sets that the player is playing before the first player
     * @param beforeFirstPlayer true if it is, else false
     */
    void setBeforeFirstPlayer(boolean beforeFirstPlayer) {
        this.beforeFirstPlayer = beforeFirstPlayer;
    }

    /**
     * Knows if the player is playing before the first player
     * @return true if it is, else false
     */
    boolean isBeforeFirstPlayer() {
        return this.beforeFirstPlayer;
    }

    /**
     * Starts the Final Frenzy mode
     */
    void startFrenzy() {
        this.frenzy = true;
    }

    /**
     * Knows if the Final Frenzy mode is active
     * @return true if it is, else false
     */
    boolean isFrenzy() {
        return this.frenzy;
    }

    /**
     * Gets the skulls number
     * @return skulls number
     */
    public int getSkulls() {
        return this.skulls;
    }

    /**
     * Sets skull number for the game
     * @param skulls number to be set
     */
    void setSkulls(int skulls) {
        this.skulls = skulls;
    }

    /**
     * Gets the squares view
     * @return List of the squares view
     */
    public List<SquareView> getSquares() {
        return new ArrayList<>(this.squares);
    }

    /**
     * Gets the square view by its coordinate
     * @param x coordinate of the square of which you want to get the square view
     * @param y coordinate of the square of which you want to get the square view
     * @return the square view with coordinate x, y
     */
    SquareView getSquareByCoordinates(int x, int y) {
        for(SquareView square : this.squares) {
            if(square.getX() == x && square.getY() == y) {
                return square;
            }
        }
        return null;
    }

    /**
     * Sets a player position
     * @param player of which you want to set the position
     * @param square where you want to set the player
     */
    void setPlayerPosition(GameCharacter player, SquareView square) {
        if (this.positions.get(player) != null) {
            this.positions.get(player).removeActivePlayer(player);
        }
        this.positions.put(player, square);
        if (square != null) {
            square.addActivePlayer(player);
        }
    }

    /**
     * Gets a character position
     * @param character of which you want to get the position
     * @return the square view of that character
     */
    SquareView getPlayerPosition(GameCharacter character) {
        return this.positions.get(character);
    }

    /**
     * Gets the killshot track
     * @return map with
     */
    Map<Integer, List<GameCharacter>> getKillshotTrack() {
        return new HashMap<>(this.killshotTrack);
    }

    void addKillshotPoints(List<GameCharacter> players, int skullNumber) {
        this.killshotTrack.put(skullNumber, players);
    }

    /**
     * Gets the arena as string
     * @return the arena as string
     */
    String arenaToString() {
        return arenaToString(new ArrayList<>());
    }

    /**
     * Writes the arena as a string
     * @param markedCoordinates borders coordinates
     * @return the string built
     */
    String arenaToString(List<Coordinates> markedCoordinates) {

        StringBuilder builder = new StringBuilder();

        builder.append("  ");
        for(int i=0; i<4; i++) {
            builder.append(center(String.valueOf(i), 23));
        }
        builder.append("\n");

        for (int i=0; i<3; i++) {
            List<List<String>> squaresStrings = new ArrayList<>();
            for (int j = 0; j < 4; j++) {
                boolean marked = false;
                SquareView square = getSquareByCoordinates(j, i);
                if (square == null) {
                    squaresStrings.add(Arrays.asList(emptySquare().split("\n")));
                } else {
                    for (Coordinates c : markedCoordinates) {
                        if (c.getX() == j && c.getY() == i) {
                            marked = true;
                            break;
                        }
                    }
                    if (marked) {
                        squaresStrings.add(Arrays.asList(square.toString(true).split("\n")));
                    } else {
                        squaresStrings.add(Arrays.asList(square.toString(false).split("\n")));
                    }
                }
            }
            if (i == 1) {
                squaresStrings.add(Arrays.asList(legendSquare(new ArrayList<>(this.positions.keySet())).split("\n")));
            }
            for (int j = 0; j < 10; j++) {
                int count = 0;
                for (List<String> square : squaresStrings) {
                    if (count == 0 && j == 4) {
                        String toAppend = i + " ";
                        builder.append(toAppend);
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

    /**
     * Writes the killshot track as a string
     * @return the string built
     */
    String killshotTrackToString() {
        StringBuilder builder = new StringBuilder();
        String toAppend = "Skulls left: " + this.skulls + "\n";
        builder.append(toAppend);
        builder.append("Kill points: ");
        for (Map.Entry<Integer, List<GameCharacter>> kill : this.killshotTrack.entrySet()) {
            int i = 0;
            for (GameCharacter c : kill.getValue()) {
                toAppend = c.getIdentifier() + " ";
                builder.append(toAppend);
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
