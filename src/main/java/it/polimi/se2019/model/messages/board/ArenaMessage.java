package it.polimi.se2019.model.messages.board;

import it.polimi.se2019.model.messages.Message;

public class ArenaMessage extends BoardMessage {

    private String arena;

    public ArenaMessage(String arena) {
        super(BoardMessageType.ARENA);
        this.arena = arena;
    }

    public String getArena() {
        return arena;
    }
}
