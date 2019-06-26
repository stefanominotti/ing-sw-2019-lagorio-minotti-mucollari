package it.polimi.se2019.model.messages.board;

/**
 * Class for handling skulls message
 */
public class SkullsMessage extends BoardMessage {

    private int skulls;

    /**
     * Class constructor, it builds a skulls message
     * @param skulls number of message
     */
    public SkullsMessage(int skulls) {
        super(BoardMessageType.SKULLS);
        this.skulls = skulls;
    }

    /**
     * Gets the skulls number of the message
     * @return the number of the skulls of the message
     */
    public int getSkulls() {
        return skulls;
    }
}