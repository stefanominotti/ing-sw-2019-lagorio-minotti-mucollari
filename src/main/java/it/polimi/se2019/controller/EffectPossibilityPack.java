package it.polimi.se2019.controller;

import it.polimi.se2019.model.*;
import javafx.scene.effect.Effect;

import java.util.List;
import java.util.Map;

public class EffectPossibilityPack {
    private final List<String> targetsAmount;
    private final List<String> effectAmount;
    private final List<GameCharacter> characters;
    private final List<Coordinates> squares;
    private final List<RoomColor> rooms;
    private final List<CardinalPoint> cardinalPoints;
    private final Map<Coordinates, List<GameCharacter>> multipleSquares;
    private final boolean require;
    private final EffectType type;

    public EffectPossibilityPack(List<String> targetsAmount, List<String> effectAmount, List<GameCharacter> characters,
                                 List<Coordinates> squares, List<RoomColor> rooms, List<CardinalPoint> cardinalPoints,
                                 Map<Coordinates, List<GameCharacter>> multipleSquares, boolean require, EffectType type) {
        this.targetsAmount = targetsAmount;
        this.effectAmount = effectAmount;
        this.characters = characters;
        this.squares = squares;
        this.rooms = rooms;
        this.cardinalPoints = cardinalPoints;
        this.multipleSquares = multipleSquares;
        this.require = require;
        this.type = type;
    }

    public List<GameCharacter> getCharacters() {
        return this.characters;
    }

    public List<Coordinates> getSquares() {
        return this.squares;
    }

    public List<RoomColor> getRooms() {
        return this.rooms;
    }

    public List<CardinalPoint> getCardinalPoints() {
        return this.cardinalPoints;
    }

    public Map<Coordinates, List<GameCharacter>> getMultipleSquares() {
        return this.multipleSquares;
    }

    public List<String> getTargetsAmount() {
        return this.targetsAmount;
    }

    public List<String> getEffectAmount() {
        return this.effectAmount;
    }

    public boolean isRequire() {
        return this.require;
    }

    public EffectType getType() {
        return this.type;
    }
}
