package it.polimi.se2019.model.messages;

public class GameSetupTimerStartedMessage extends Message {

    private long time;

    public GameSetupTimerStartedMessage(long time) {
        setMessageType(this.getClass());
        this.time = time;
    }

    public long getTime() {
        return this.time;
    }
}
