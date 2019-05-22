package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.GameCharacter;

import java.util.List;

public class CharacterMessage extends Message {

    private final List<GameCharacter> availables;
    private final GameCharacter character;
    private final String token;

    public CharacterMessage(List<GameCharacter> availables) {
        setMessageType(this.getClass());
        this.availables = availables;
        this.character = null;
        this.token = null;
    }

    public CharacterMessage(GameCharacter character, String token) {
        setMessageType(this.getClass());
        this.availables = null;
        this.character = character;
        this.token = token;
    }

    public List<GameCharacter> getAvailables() {
        return this.availables;
    }

    public GameCharacter getCharacter() {
        return this.character;
    }

    public String getToken() {
        return this.token;
    }
}
