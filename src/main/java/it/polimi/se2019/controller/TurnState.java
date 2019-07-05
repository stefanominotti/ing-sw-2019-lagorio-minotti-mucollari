package it.polimi.se2019.controller;

/**
 * Enumeration class for handling turn state
 * @author stefanominotti
 */
public enum TurnState {

    SELECTACTION,
    FIRST_RESPAWNING,
    DEATH_RESPAWNING,
    POWERUPACTIVATED,
    MOVING,
    PICKINGUP,
    ENDING;
}
