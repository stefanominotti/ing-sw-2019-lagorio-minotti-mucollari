package it.polimi.se2019.model.messages;

public class SkullsMessage extends Message {

    private int skulls;

    public SkullsMessage(int skulls) {
        setMessageType(this.getClass());
        this.skulls = skulls;
    }

    public int getSkulls() {
        return skulls;
    }
}
