package it.polimi.se2019.controller;

import it.polimi.se2019.model.*;
import it.polimi.se2019.model.arena.CardinalPoint;
import it.polimi.se2019.model.arena.Coordinates;
import it.polimi.se2019.model.arena.RoomColor;
import it.polimi.se2019.model.playerassets.weapons.EffectType;

import java.io.Serializable;
import java.util.*;

/**
 * Class for handling effect possibility pack, with all effect data
 * @author eknidmucollari
 */
public class EffectPossibilityPack implements Serializable {
    private List<String> targetsAmount;
    private List<GameCharacter> characters;
    private List<Coordinates> squares;
    private List<RoomColor> rooms;
    private List<CardinalPoint> cardinalPoints;
    private Map<Coordinates, List<GameCharacter>> multipleSquares;
    private boolean require;
    private EffectType type;
    private String description;

    /**
     * Class constructor, it is used by the server to build an effect possibility pack
     * @param targetsAmount available for this effect
     * @param characters target available for this effect
     * @param squares available for this effect
     * @param rooms available for this effect
     * @param cardinalPoints available for this effect
     * @param multipleSquares map for this effect
     * @param require true if the effect is mandatory to use, else false
     * @param type of the effect
     */
    EffectPossibilityPack(List<String> targetsAmount, List<GameCharacter> characters,
                                 List<Coordinates> squares, List<RoomColor> rooms, List<CardinalPoint> cardinalPoints,
                                 Map<Coordinates, List<GameCharacter>> multipleSquares, boolean require, EffectType type,
                                 String description) {
        this.targetsAmount = targetsAmount;
        this.characters = characters;
        this.squares = squares;
        this.rooms = rooms;
        this.cardinalPoints = cardinalPoints;
        this.multipleSquares = multipleSquares;
        this.require = require;
        this.type = type;
        this.description = description;
    }

    /**
     * Class constructor, it is used to build an empty possibility pack and is fill by client, time by time
     * @param require true if the effect is mandatory to use, else false
     * @param type of the effect
     */
    public EffectPossibilityPack(boolean require, EffectType type) {
        this.characters = new ArrayList<>();
        this.squares = new ArrayList<>();
        this.rooms = new ArrayList<>();
        this.cardinalPoints = new ArrayList<>();
        this.multipleSquares = new LinkedHashMap<>();
        this.require = require;
        this.type = type;
    }

    /**
     * Gets the effect targets
     * @return List of the characters target
     */
    public List<GameCharacter> getCharacters() {
        return this.characters;
    }

    /**
     * Gets the available squares for the effect
     * @return list of coordinates of the available squares
     */
    public List<Coordinates> getSquares() {
        return this.squares;
    }

    /**
     * Gets the available rooms for the effect
     * @return List of colors of the available rooms
     */
    public List<RoomColor> getRooms() {
        return this.rooms;
    }

    /**
     * Gets the available cardinal points for the effect
     * @return List of the available cardinal points
     */
    public List<CardinalPoint> getCardinalPoints() {
        return this.cardinalPoints;
    }

    /**
     * Gets a map for applying effects with "multiple squares" position constraint
     * @return Map with coordinates and list of game characters for that coordinates
     */
    public Map<Coordinates, List<GameCharacter>> getMultipleSquares() {
        return this.multipleSquares;
    }

    /**
     * Gets the target amount for the effect
     * @return List of targets amount
     */
    public List<String> getTargetsAmount() {
        return this.targetsAmount;
    }

    /**
     * Knows if the effect is mandatory
     * @return true if it is, else false
     */
    public boolean isRequire() {
        return this.require;
    }

    /**
     * Gets the effect type
     * @return type of the effect
     */
    public EffectType getType() {
        return this.type;
    }

    /**
     * Sets the target characters for the effect
     * @param characters list you want to set as targets
     */
    public void setCharacters(List<GameCharacter> characters) {
        this.characters = characters;
    }

    /**
     * Sets the available squares for the effect
     * @param squares list of coordinates which you want to make available for the effect
     */
    public void setSquares(List<Coordinates> squares) {
        this.squares = squares;
    }

    /**
     * Sets the available rooms color for the effect
     * @param rooms color list available for the effect
     */
    public void setRooms(List<RoomColor> rooms) {
        this.rooms = rooms;
    }

    /**
     * Sets the available cardinal points for the effect
     * @param cardinalPoints list available for the effect
     */
    public void setCardinalPoints(List<CardinalPoint> cardinalPoints) {
        this.cardinalPoints = cardinalPoints;
    }

    /**
     * Sets the effect possibility as mandatory
     * @param require true if you want to set mandatory, else false
     */
    public void setRequire(boolean require) {
        this.require = require;
    }

    /**
     * Gets the effect macro description
     * @return String with the effect macro description
     */
    public String getDescription() { return this.description; }
}

