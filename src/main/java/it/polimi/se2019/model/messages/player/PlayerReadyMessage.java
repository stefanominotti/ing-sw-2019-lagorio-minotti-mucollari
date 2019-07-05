package it.polimi.se2019.model.messages.player;

import it.polimi.se2019.model.GameCharacter;

/**
 * Class for handling player ready message
 * @author stefanominotti
 */
public class PlayerReadyMessage extends PlayerMessage {

    private String nickname;

    /**
     * Class constructor, it builds a player ready message when a player chose a nickname
     * @param character of the ready player
     * @param nickname of the ready player
     */
    public PlayerReadyMessage(GameCharacter character, String nickname) {
        super(PlayerMessageType.READY, character);
        this.nickname = nickname;
    }

    /**
     * Gets the nickname of the ready player
     * @return the nickname of the ready player
     */
    public String getNickname() {
        return this.nickname;
    }
}
