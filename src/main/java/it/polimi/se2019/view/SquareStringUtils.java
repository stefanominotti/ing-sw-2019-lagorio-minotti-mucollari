package it.polimi.se2019.view;

import it.polimi.se2019.model.GameCharacter;

import java.util.List;

public class SquareStringUtils {

    static String emptySquare() {
        StringBuilder builder = new StringBuilder();
        for (int i=0; i<10; i++) {
            builder.append("                       \n");
        }
        return builder.toString();
    }

    static String legendSquare(List<GameCharacter> characters) {
        StringBuilder builder = new StringBuilder();
        int i;
        for (i=0; i<3; i++) {
            builder.append("                       \n");
        }
        for (GameCharacter character : characters) {
            builder.append(center(character.getIdentifier() + " - " + character.toString(), 23) + "\n");
            i++;
        }
        while (i<10) {
            builder.append("                       \n");
            i++;
        }
        return builder.toString();
    }

    static String center(String text, int len){
        if (len <= text.length())
            return text.substring(0, len);
        int before = (len - text.length())/2;
        if (before == 0)
            return String.format("%-" + len + "s", text);
        int rest = len - before;
        return String.format("%" + before + "s%-" + rest + "s", "", text);
    }
}
