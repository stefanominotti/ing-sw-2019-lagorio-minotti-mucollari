package it.polimi.se2019.view;

import it.polimi.se2019.model.AmmoType;
import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.Weapon;

import java.util.*;

public class PlayerBoard {

    private GameCharacter character;
    private String name;
    private Map<AmmoType, Integer> availableAmmos;
    private Map<GameCharacter, Integer> revengeMarks;
    private List<GameCharacter> damages;
    private List<Integer> killshotPoints;
    private List<Weapon> unloadedWeapons;
    int weaponsNumber;
    int powerupsNumber;

    PlayerBoard(GameCharacter character, String name) {
        this.name = name;
        this.character = character;
        this.damages = new ArrayList<>();
        this.killshotPoints = new ArrayList<>(Arrays.asList(8, 6, 4, 2, 1, 1));
        this.availableAmmos = new EnumMap<>(AmmoType.class);
        this.availableAmmos.put(AmmoType.BLUE, 1);
        this.availableAmmos.put(AmmoType.RED, 1);
        this.availableAmmos.put(AmmoType.YELLOW, 1);
        this.revengeMarks = new HashMap<>();
        this.weaponsNumber = 0;
        this.powerupsNumber = 0;
        this.unloadedWeapons = new ArrayList<>();
    }

    void addDamage(GameCharacter attacker, GameCharacter target, int amount) {}

    void addMarks(GameCharacter marker, GameCharacter target, int amount) {}

    void removeMarks(GameCharacter marker, GameCharacter target) {}

    void addAmmos(GameCharacter player, Map<AmmoType, Integer> ammos) {}

    void useAmmos(GameCharacter player, Map<AmmoType, Integer> usedAmmos) {}
}
