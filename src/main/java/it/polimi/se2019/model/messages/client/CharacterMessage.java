package it.polimi.se2019.model.messages.client;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.messages.Message;

import java.util.List;

public class CharacterMessage extends ClientMessage {

    private final List<GameCharacter> availables;
    private final String token;

    public CharacterMessage(List<GameCharacter> availables) {
        super(ClientMessageType.CHARACTER_SELECTION, null);
        this.availables = availables;
        this.token = null;
    }

    public CharacterMessage(GameCharacter character, String token) {
        super(ClientMessageType.CHARACTER_SELECTION, character);
        this.availables = null;
        this.token = token;
    }

    public List<GameCharacter> getAvailables() {
        return this.availables;
    }

    public String getToken() {
        return this.token;
    }
}
