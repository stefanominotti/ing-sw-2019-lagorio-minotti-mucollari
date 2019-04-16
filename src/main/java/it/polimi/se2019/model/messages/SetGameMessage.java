package it.polimi.se2019.model.messages;

public class SetGameMessage extends Message {

    private final int skulls;
    private final int arena;

    public SetGameMessage(int skulls, int arena) {
        setMessageType(this.getClass());
        this.skulls = skulls;
        this.arena = arena;
    }

    public int getSkulls() { return skulls; }

    public int getArena() { return arena; }
}
