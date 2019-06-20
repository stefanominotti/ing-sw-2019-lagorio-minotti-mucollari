package it.polimi.se2019.model.messages.board;

public class SkullsMessage extends BoardMessage {

    private int skulls;

    public SkullsMessage(int skulls) {
        super(BoardMessageType.SKULLS);
        this.skulls = skulls;
    }

    public int getSkulls() {
        return skulls;
    }
}
