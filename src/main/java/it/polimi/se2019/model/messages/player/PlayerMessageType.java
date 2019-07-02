package it.polimi.se2019.model.messages.player;

/**
 * Enumeration Class for handling player message type
 */
public enum PlayerMessageType {
    READY,
    CREATED,
    SPAWNED,
    MASTER_CHANGED,
    START_SETUP,
    SCORE,
    SKULLS_SET,
    ATTACK,
    MARKS_TO_DAMAGES,
    FRENZY,
    KILLSHOT_POINTS,
    DEATH,
    BOARD_FLIP,
    MOVE
}