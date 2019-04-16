package it.polimi.se2019.model.messages;

import it.polimi.se2019.model.GameCharacter;

import java.util.List;

public class PlayerCreatedMessage extends Message {

    private final GameCharacter character;
    private final List<String> nicknames;

    public PlayerCreatedMessage(GameCharacter character, List<String> nicknames) {
        setMessageType(this.getClass());
        this.character = character;
        this.nicknames = nicknames;
    }

    public GameCharacter getCharacter() {
        return this.character;
    }

    public List<String> getNicknames() {return this.nicknames; }
}
