package it.polimi.se2019.model.messages.client;

import it.polimi.se2019.model.GameCharacter;

import java.util.List;

/**
 * Class for handling character message
 * @author stefanominotti
 */
public class CharacterMessage extends ClientMessage {

    private final List<GameCharacter> availables;
    private final String token;

    /**
     * Class constructor, it builds a character message to show which characters can be chosen
     * @param availables game characters
     */
    public CharacterMessage(List<GameCharacter> availables) {
        super(ClientMessageType.CHARACTER_SELECTION, null);
        this.availables = availables;
        this.token = null;
    }

    /**
     * Class constructor, it builds a character message to handle character chosen, and the client token
     * @param character which the player has chosen
     * @param token of the client who performed the choice
     */
    public CharacterMessage(GameCharacter character, String token) {
        super(ClientMessageType.CHARACTER_SELECTION, character);
        this.availables = null;
        this.token = token;
    }

    /**
     * Gets the available characters for choice
     * @return List of the available characters
     */
    public List<GameCharacter> getAvailables() {
        return this.availables;
    }

    /**
     * Gets the token of the client who performed the choice
     * @return the token of the client
     */
    public String getToken() {
        return this.token;
    }
}
