package it.polimi.se2019.model.messages.client;

/**
 * Enumeration Class for handling client message type
 * @author stefanominotti
 */
public enum ClientMessageType {
    READY,
    RECONNECTED,
    DISCONNECTED,
    GAME_ALREADY_STARTED,
    LOAD_VIEW,
    CLIENT_RECONNECTION,
    LOBBY_FULL,
    INVALID_TOKEN,
    CHARACTER_SELECTION
}
