package it.polimi.se2019.controller;

import it.polimi.se2019.controller.EffectPossibilityPack;
import it.polimi.se2019.controller.EffectsController;
import it.polimi.se2019.controller.TurnController;
import it.polimi.se2019.model.*;
import it.polimi.se2019.server.GameLoader;
import org.junit.Before;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class AppTest {

    Weapon weapon;
    Board board;
    Player player;
    TurnController turnController;
    EffectsController controller;
    EffectPossibilityPack pack;
    //Classe temporanea per fare TEST random

    @Test
    public void tempTestShokwave() {
        this.weapon = Weapon.SHOCKWAVE;
        try {
            this.board = (new GameLoader()).loadBoard();
            this.player = this.board.getPlayers().get(this.board.getCurrentPlayer());
            this.turnController = new TurnController(this.board, null, null);
            this.controller = new EffectsController(this.board,null);
            this.controller.setActivePlayer(this.board.getPlayers().get(this.board.getCurrentPlayer()));
            List<WeaponEffect> effects = new ArrayList<>();
            for (WeaponEffect effect : weapon.getPrimaryEffect()) {
                effects.add(weapon.getPrimaryEffect().get(0));
                controller.setEffectsQueue(effects);
                this.pack = this.controller.seeEffectPossibility(effect);
                handleMultipleSquareRequest();
            }
        }catch(UnsupportedOperationException e){}
    }

    private void handleMultipleSquareRequest() {
        List<String> targetsAmaunt = this.pack.getTargetsAmount();
        StringBuilder text = new StringBuilder();
        if(targetsAmaunt.size() == 1) {
            int amaunt = Integer.parseInt(targetsAmaunt.get(0));
            text.append("Choose " + amaunt + " players each in different squares");
        } else if (targetsAmaunt.get(1) == "MAX") {
            int min = Integer.parseInt(targetsAmaunt.get(0));
            text.append("Choose at least " + min + " players each in different squares");
        } else {
            int min = Integer.parseInt(targetsAmaunt.get(0));
            int max = Integer.parseInt(targetsAmaunt.get(1));
            text.append("Choose from " + min + " to " + max + " players each in different squares");
        }
        text.append("type square_index,character_index/... :\n");
        int squareIndex = 1;
        for (Map.Entry<Coordinates, List<GameCharacter>> square : this.pack.getMultipleSquares().entrySet()) {
            text.append("[" + squareIndex + "]-from [" + square.getKey().getX() + "," + square.getKey().getY() + "]:\n");
            int characterIndex = 1;
            for(GameCharacter character : square.getValue()) {
                text.append("[" + characterIndex + "]- " + character + "\n");
                characterIndex++;
            }
            squareIndex++;
        }
        text.setLength(text.length() - 1);
        System.out.println(text.toString());
        String input = "2,3";
        handleEffectMultipleSquaresInput(input);
    }

    private void handleEffectMultipleSquaresInput(String input) {
        String[] inputList = input.split("/");
        Map<Coordinates, List<GameCharacter>> availableCharacters = new LinkedHashMap<>(this.pack.getMultipleSquares());
        List<GameCharacter> selectedCharacters = new ArrayList<>();
        List<Coordinates> availableSquares = new ArrayList<>(this.pack.getMultipleSquares().keySet());
        try {
            for (String i : inputList) {
                String[] strings = i.split(",");
                int squareIndex = Integer.parseInt(strings[0]) - 1;
                int characterIndex = Integer.parseInt(strings[1]) - 1;
                if(squareIndex < 0 || characterIndex < 0 || squareIndex >= availableCharacters.size()) {
                    System.out.println("Invalid input, retry: ");
                    return;
                }
                for(Map.Entry<Coordinates, List<GameCharacter>> characters : availableCharacters.entrySet()) {
                    if(squareIndex == 0 && availableSquares.contains(characters.getKey()) && characterIndex < characters.getValue().size()) {
                        selectedCharacters.add(characters.getValue().get(characterIndex));
                        availableSquares.remove(characters.getKey());
                        break;
                    } else if(squareIndex < 0 || squareIndex == 0 && (!availableSquares.contains(characters.getKey()) || characterIndex >= characters.getValue().size())) {
                        System.out.println("Invalid input, retry: ");
                        return;
                    }
                    squareIndex--;
                }
            }
            List<String> targetsAmount = this.pack.getTargetsAmount();
            if(targetsAmount.size() == 1 && selectedCharacters.size() != Integer.parseInt(targetsAmount.get(0)) ||
                    targetsAmount.size() > 1 && (selectedCharacters.size() < Integer.parseInt(targetsAmount.get(0)) ||
                            (!targetsAmount.get(1).equals("MAX") && selectedCharacters.size() > Integer.parseInt(targetsAmount.get(1))))) {
                System.out.println("Invalid input, retry: ");
                return;
            }
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            System.out.println("Invalid input, retry: ");
            return;
        }
        for(GameCharacter character : selectedCharacters) {
            System.out.println(character);
        }
    }

    @Test
    public void tempTestTractorBeam() {
        this.weapon = Weapon.TRACTOR_BEAM;
        try {
            this.board = (new GameLoader()).loadBoard();
            this.player = this.board.getPlayers().get(this.board.getCurrentPlayer());
            this.turnController = new TurnController(this.board, null, null);
            this.controller = new EffectsController(this.board,null);
            this.controller.setActivePlayer(this.board.getPlayers().get(this.board.getCurrentPlayer()));
            List<WeaponEffect> effects = new ArrayList<>();
            for (WeaponEffect effect : weapon.getPrimaryEffect()) {
                effects.add(weapon.getPrimaryEffect().get(0));
                controller.setEffectsQueue(effects);
                this.pack = this.controller.seeEffectPossibility(effect);
            }
        }catch(UnsupportedOperationException e){}
    }

}
