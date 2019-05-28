package it.polimi.se2019.controller;

import it.polimi.se2019.model.CardinalPoint;
import it.polimi.se2019.model.Coordinates;
import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.RoomColor;

import java.util.List;
import java.util.Map;

public class EffectPossibilityPack {
    List<String> targetsAmount;
    List<String> effectAmount;
    List<GameCharacter> characters;
    List<Coordinates> squares;
    List<RoomColor> rooms;
    List<CardinalPoint> cardinalPoints;
    Map<Coordinates, List<GameCharacter>> multipleSquares;

    public EffectPossibilityPack(List<String> targetsAmount, List<String> effectAmount, List<GameCharacter> characters, List<Coordinates> squares, List<RoomColor> rooms,
                                 List<CardinalPoint> cardinalPoints, Map<Coordinates, List<GameCharacter>> multipleSquares) {
        this.targetsAmount = targetsAmount;
        this.effectAmount = effectAmount;
        this.characters = characters;
        this.squares = squares;
        this.rooms = rooms;
        this.cardinalPoints = cardinalPoints;
        this.multipleSquares = multipleSquares;
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
}
