package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.GameCharacter;

import java.util.List;

public class PlayerListMessage extends Message {

    private final List<GameCharacter> characters;

    public PlayerListMessage(List<GameCharacter> characters) {
        setMessageType(this.getClass());
        this.characters = characters;
    }

    public List<GameCharacter> getCharacters() {
        return this.characters;
    }
}
