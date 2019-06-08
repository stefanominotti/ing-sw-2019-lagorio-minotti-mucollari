package it.polimi.se2019.controller;

import it.polimi.se2019.model.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EffectPossibilityPack implements Serializable {
    private List<String> targetsAmount;
    private List<GameCharacter> characters;
    private List<Coordinates> squares;
    private List<RoomColor> rooms;
    private List<CardinalPoint> cardinalPoints;
    private Map<Coordinates, List<GameCharacter>> multipleSquares;
    private boolean require;
    private EffectType type;

    public EffectPossibilityPack(List<String> targetsAmount, List<GameCharacter> characters,
                                 List<Coordinates> squares, List<RoomColor> rooms, List<CardinalPoint> cardinalPoints,
                                 Map<Coordinates, List<GameCharacter>> multipleSquares, boolean require, EffectType type) {
        this.targetsAmount = targetsAmount;
        this.characters = characters;
        this.squares = squares;
        this.rooms = rooms;
        this.cardinalPoints = cardinalPoints;
        this.multipleSquares = multipleSquares;
        this.require = require;
        this.type = type;
    }

    public EffectPossibilityPack(boolean require, EffectType type) {
        this.characters = new ArrayList<>();
        this.squares = new ArrayList<>();
        this.rooms = new ArrayList<>();
        this.cardinalPoints = new ArrayList<>();
        this.multipleSquares = new HashMap<>();
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

    public boolean isRequire() {
        return this.require;
    }

    public EffectType getType() {
        return this.type;
    }

    public void setCharacters(List<GameCharacter> characters) {
        this.characters = characters;
    }

    public void setSquares(List<Coordinates> squares) {
        this.squares = squares;
    }

    public void setRooms(List<RoomColor> rooms) {
        this.rooms = rooms;
    }

    public void setCardinalPoints(List<CardinalPoint> cardinalPoints) {
        this.cardinalPoints = cardinalPoints;
    }

    public void setMultipleSquares(Map<Coordinates, List<GameCharacter>> multipleSquares) {
        this.multipleSquares = multipleSquares;
    }

    public void setRequire(boolean require) {
        this.require = require;
    }
}
