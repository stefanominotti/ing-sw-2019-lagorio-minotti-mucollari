package it.polimi.se2019.view;

import it.polimi.se2019.model.GameCharacter;

import java.util.List;

/**
 * Class for handling square string utils
 */
public class SquareStringUtils {

    private static final String BLANK_ROW = "                       \n";

    /**
     * Class constructor
     */
    private SquareStringUtils() {}

    /**
     * Draws an empty square as a string
     * @return empty square drawn
     */
    public static String emptySquare() {
        StringBuilder builder = new StringBuilder();
        for (int i=0; i<10; i++) {
            builder.append(BLANK_ROW);
        }
        return builder.toString();
    }

    /**
     * Draws the legend of the game characters playing the game
     * @param characters playing
     * @return legend drawn
     */
    public static String legendSquare(List<GameCharacter> characters, List<GameCharacter> deadPlayers) {
        StringBuilder builder = new StringBuilder();
        int i;
        for (i=0; i<3; i++) {
            builder.append(BLANK_ROW);
        }
        for (GameCharacter character : characters) {
            String toAppend;
            if (deadPlayers.contains(character)) {
                toAppend = center(character.getIdentifier() + " - " + character.toString() + " [DEAD]",
                        23) + "\n";
            } else {
                toAppend = center(character.getIdentifier() + " - " + character.toString(), 23) + "\n";
            }
            builder.append(toAppend);
            i++;
        }
        while (i<10) {
            builder.append(BLANK_ROW);
            i++;
        }
        return builder.toString();
    }

    /**
     * Centers a text on a line
     * @param text to be centered
     * @param len of the line
     * @return test centered
     */
    public static String center(String text, int len){
        if (len <= text.length())
            return text.substring(0, len);
        int before = (len - text.length())/2;
        String format;
        if (before == 0) {
            format = "%-" + len + "s";
            return String.format(format, text);
        }
        int rest = len - before;
        format = "%" + before + "s%-" + rest + "s";
        return String.format(format, "", text);
    }
}
