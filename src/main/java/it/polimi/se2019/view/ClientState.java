package it.polimi.se2019.view;

/**
 * Enumeration class for handling client state
 *  @author stefanominotti
 *  @author eknidmucollari
 */
public enum ClientState {
    TYPING_NICKNAME,
    CHOOSING_CHARACTER,
    WAITING_START,
    SETTING_SKULLS,
    SETTING_ARENA,
    WAITING_SETUP,
    OTHER_PLAYER_TURN,
    YOUR_TURN,
    DISCARD_SPAWN,
    SELECT_ACTION,
    SELECT_BOARD_TO_SHOW,
    SELECT_MOVEMENT,
    SELECT_PICKUP,
    SELECT_WEAPON,
    SWITCH_WEAPON,
    RECHARGE_WEAPON,
    PAYMENT,
    RECONNECTING,
    USE_POWERUP,
    SELECT_POWERUP_POSITION,
    SELECT_POWERUP_TARGET,
    USE_WEAPON,
    USE_EFFECT,
    EFFECT_COMBO_SELECTION,
    EFFECT_REQUIRE_SELECTION,
    EFFECT_SELECT_SQUARE,
    EFFECT_SELECT_ROOM,
    EFFECT_SELECT_CARDINAL,
    EFFECT_MOVE_SELECTION,
    MULTIPLE_SQUARES_SELECTION,
    MULTIPLE_POWERUPS_SELECTION,
    PERSISTENCE_SELECTION,
    EFFECT_TARGET_SELECTION
}
