package it.polimi.se2019.model.messages.board;

/**
 * Class for handling arena message
 */
public class ArenaMessage extends BoardMessage {

    private String arena;

    /**
     * Class constructor, it builds an arena message
     * @param arena name of the arena
     */
    public ArenaMessage(String arena) {
        super(BoardMessageType.ARENA);
        this.arena = arena;
    }

    /**
     * Gets the arena of the message
     * @return the name of the arena
     */
    public String getArena() {
        return arena;
    }
}