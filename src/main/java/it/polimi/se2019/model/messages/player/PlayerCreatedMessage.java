package it.polimi.se2019.model.messages.player;

import it.polimi.se2019.model.GameCharacter;
import java.util.Map;

/**
 * Class for handling player created message
 */
public class PlayerCreatedMessage extends PlayerMessage {

    private String nickname;
    private Map<GameCharacter, String> otherPlayers;

    /**
     * Class constructor, it builds a player created message
     * @param character chosen by the created player
     * @param nickname chosen by the created player
     * @param otherPlayers map with the other players nickname-character
     */
    public PlayerCreatedMessage(GameCharacter character, String nickname, Map<GameCharacter, String> otherPlayers) {
        super(PlayerMessageType.CREATED, character);
        this.nickname = nickname;
        this.otherPlayers = otherPlayers;
    }

    /**
     * Gets the nickname of the created player
     * @return the nickname of the created player
     */
    public String getNickname() {
        return this.nickname;
    }

    /**
     * Gets the other players nickname-character
     * @return Map with the game character and his corresponding nickname
     */
    public Map<GameCharacter, String> getOtherPlayers() {
        return this.otherPlayers;
    }
}
