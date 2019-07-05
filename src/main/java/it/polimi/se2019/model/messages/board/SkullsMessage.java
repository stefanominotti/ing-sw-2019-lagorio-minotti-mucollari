package it.polimi.se2019.model.messages.board;

/**
 * Class for handling skulls message
 * @author stefanominotti
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
     * Gets the current skulls number
     * @return the number of the skulls
     */
    public int getSkulls() {
        return skulls;
    }
}