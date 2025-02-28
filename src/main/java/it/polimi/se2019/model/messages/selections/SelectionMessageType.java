package it.polimi.se2019.model.messages.selections;

/**
 * Class for handling selection message type
 * @author stefanominotti
 */
public enum SelectionMessageType {
    PICKUP,
    MOVE,
    RELOAD,
    PICKUP_WEAPON,
    SWITCH,
    POWERUP_TARGET,
    POWERUP_POSITION,
    USE_POWERUP,
    DISCARD_POWERUP,
    ACTION,
    USE_WEAPON, EFFECT,
    EFFECT_COMBO,
    EFFECT_POSSIBILITY,
    PERSISTENCE
}
