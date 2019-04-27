package it.polimi.se2019.model.messages;

public class ArenaMessage extends Message {

    private String arena;

    public ArenaMessage(String arena) {
        setMessageType(this.getClass());
        this.arena = arena;
    }

    public String getArena() {
        return arena;
    }
}
