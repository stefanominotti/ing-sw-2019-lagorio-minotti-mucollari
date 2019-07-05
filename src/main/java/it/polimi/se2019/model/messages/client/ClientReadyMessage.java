package it.polimi.se2019.model.messages.client;

import it.polimi.se2019.model.GameCharacter;

/**
 * Class for handling client ready message
 * @author eknidmucollari
 */
public class ClientReadyMessage extends ClientMessage {

    private String nickname;
    private String token;

    /**
     * Class constructor, it builds a client ready message to send the client data
     * @param character chosen
     * @param nickname chosen
     * @param token of the client
     */
    public ClientReadyMessage(GameCharacter character, String nickname, String token) {
        super(ClientMessageType.READY, character);
        this.nickname = nickname;
        this.token = token;
    }

    /**
     * Gets the nickname chosen by the client
     * @return the nickname chosen by the client
     */
    public String getNickname() {
        return this.nickname;
    }

    /**
     * Gets the token of the client
     * @return the token of the client
     */
    public String getToken() {
        return this.token;
    }
}