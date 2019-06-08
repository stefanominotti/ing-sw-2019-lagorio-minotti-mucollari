package it.polimi.se2019.controller;

import it.polimi.se2019.model.*;
import it.polimi.se2019.server.GameLoader;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EffectControllerTest {
    Weapon weapon;
    Board board;
    Player player;
    TurnController turnController;
    EffectsController controller;
    String configPath = System.getProperty("user.home");

    @Test
    public void weaponTest() {
        String filePath;
        for(Weapon weapon : Weapon.values()) {
            filePath = configPath.replace('\\', '/') + "/WeaponsTest/" + weapon + "Test.json" ;
            try {
                FileWriter writer = new FileWriter(configPath + "/" + "config.json");
                writer.write("{" + "\"path\": \"" + filePath + "\"" + "}");
                writer.flush();
            } catch (IOException e) {
            }
            System.out.println(weapon.getName());
            weaponPrimaryTest();
            weaponAlternativeTest();
        }
    }

    private List<GameCharacter> chooseCharacters(EffectPossibilityPack pack) {
        List<GameCharacter> targets = new ArrayList<>();
        List<String> targetAmaount = pack.getTargetsAmount();
        if(pack.getCharacters().get(0) == this.player.getCharacter()){
            targets.add(this.player.getCharacter());
            return targets;
        }
        if(targetAmaount.size() == 1) {
            if (targetAmaount.get(0).equals("MAX")) {
                targets = new ArrayList<>(pack.getCharacters());
            } else {
                int amount = Integer.parseInt(targetAmaount.get(0));
                int i = 0;
                while (i < amount) {
                    targets.add(pack.getCharacters().get(i));
                    i++;
                }
            }
        } else {
            int min = Integer.parseInt(targetAmaount.get(0));
            int max;
            if (targetAmaount.get(1).equals("MAX")) {
                max = pack.getCharacters().size();
            } else {
                max = Integer.parseInt(targetAmaount.get(1));
            }
            for(int i = min; i <= max; i++) {
                targets.add(pack.getCharacters().get(i-min));
            }

        }
        return targets;
    }

    private Coordinates chooseSquare (EffectPossibilityPack pack) {
        return pack.getSquares().get(0);
    }

    private RoomColor chooseRoom (EffectPossibilityPack pack) {
        return  pack.getRooms().get(0);
    }

    private CardinalPoint chooseCardinalPoint (EffectPossibilityPack pack) {
        return pack.getCardinalPoints().get(0);
    }

    private Map<Coordinates, List<GameCharacter>> chooseMultipleSquares(EffectPossibilityPack pack) {
        Map<Coordinates, List<GameCharacter>> multiSquare = new HashMap<>();
        for (Coordinates square : multiSquare.keySet()) {
            multiSquare.put(square, chooseCharacters(pack));
        }
        return multiSquare;
    }

    private void apply (EffectPossibilityPack pack) {
        List<GameCharacter> targets = new ArrayList<>();
        List<Coordinates> coordinates = new ArrayList<>();
        List<RoomColor> rooms = new ArrayList<>();
        List<CardinalPoint> cardinalPoints = new ArrayList<>();
        Map<Coordinates, List<GameCharacter>> multipleSquares = new HashMap<>();
        switch (pack.getType()) {
            case MOVE:
                coordinates.add(chooseSquare(pack));
                targets = chooseCharacters(pack);
                break;
            case SELECT:
                if(!pack.getSquares().isEmpty()) {
                    coordinates.add(chooseSquare(pack));
                }
                if(!pack.getRooms().isEmpty()) {
                    rooms.add(chooseRoom(pack));
                }
                if(!pack.getCardinalPoints().isEmpty()) {
                    cardinalPoints.add(chooseCardinalPoint(pack));
                }
                break;
            case MARK:
                targets = chooseCharacters(pack);
                break;
            case DAMAGE:
                if(pack.getMultipleSquares().isEmpty()) {
                    targets = chooseCharacters(pack);
                } else {
                    multipleSquares = chooseMultipleSquares(pack);
                }
        }
        controller.effectApplication(new EffectPossibilityPack(pack.getTargetsAmount(),
                targets, coordinates, rooms, cardinalPoints, multipleSquares, true, pack.getType()));
    }

    private void scanEffects(List<WeaponEffect> effectsList) throws UnsupportedOperationException {
        List<WeaponEffect> effects = new ArrayList<>();
        for(WeaponEffect effect : effectsList) {
            effects.add(weapon.getPrimaryEffect().get(0));
            controller.setEffectsQueue(effects);
            EffectPossibilityPack pack;
            try {
                pack = controller.seeEffectPossibility(effect);
                printPack(pack);
                apply(pack);
            } catch (UnsupportedOperationException e) {
                if(effect.getEffectName() == null && !effect.isRequired()) {
                    System.out.println("no targets available");
                } else {
                    throw new UnsupportedOperationException();
                }
            }
        }
    }

    private void printPack(EffectPossibilityPack pack) {
        System.out.println("-----------\n" + pack.getType());
        for(GameCharacter el : pack.getCharacters()) {
            System.out.println(el);
        }
        for(Coordinates el : pack.getSquares()) {
            System.out.println(el.getX() + "," + el.getY());
        }
        for(RoomColor el : pack.getRooms()) {
            System.out.println(el);
        }
        for(CardinalPoint el : pack.getCardinalPoints()) {
            System.out.println(el);
        }
        for(Map.Entry<Coordinates, List<GameCharacter>> el : pack.getMultipleSquares().entrySet()) {
            System.out.println("{\n["+ el.getKey().getX() + "," + el.getKey().getY() + "]");
            for(GameCharacter ca : el.getValue()) {
                System.out.println(ca);
            }
            System.out.println("}");
        }
        System.out.println("-----------");
    }

    public void weaponPrimaryTest() {
        this.board = (new GameLoader()).loadBoard();
        this.player = this.board.getPlayers().get(this.board.getCurrentPlayer());
        this.weapon = player.getWeapons().get(0).getWeaponType();
        this.turnController = new TurnController(this.board, null, null);
        this.controller = new EffectsController(this.board,null);
        this.controller.setActivePlayer(this.board.getPlayers().get(this.board.getCurrentPlayer()));
        try {
            this.controller.setEffectOrder(WeaponEffectOrderType.PRIMARY);
            scanEffects(weapon.getPrimaryEffect());
            System.out.println("*************");
        }
        catch (UnsupportedOperationException e) {
            System.out.println("effets no available");
        }
        try {
            this.controller.setEffectOrder(WeaponEffectOrderType.SECONDARYONE);
            scanEffects(weapon.getSecondaryEffectOne());
            System.out.println("*************");
        }
        catch (IllegalStateException ignore) {}
        catch (UnsupportedOperationException e) {
            System.out.println("effets no available");
        }
        try {
            this.controller.setEffectOrder(WeaponEffectOrderType.SECONDARYTWO);
            scanEffects(weapon.getSecondaryEffectTwo());
            System.out.println("*************");
        }
        catch (IllegalStateException ignore) {}
        catch (UnsupportedOperationException e) {
            System.out.println("effets no available");
        }

    }

    public void weaponAlternativeTest() {
        this.board = (new GameLoader()).loadBoard();
        this.player = this.board.getPlayers().get(this.board.getCurrentPlayer());
        this.weapon = player.getWeapons().get(0).getWeaponType();
        this.turnController = new TurnController(this.board, null, null);
        this.controller = new EffectsController(this.board, null);
        this.controller.setActivePlayer(this.board.getPlayers().get(this.board.getCurrentPlayer()));
        try {
            this.controller.setEffectOrder(WeaponEffectOrderType.PRIMARY);
            scanEffects(weapon.getAlternativeMode());
            System.out.println("*************");
        }
        catch (IllegalStateException ignore) {}
        catch (UnsupportedOperationException e) {
            System.out.println("effets no available");
        }
    }
}
