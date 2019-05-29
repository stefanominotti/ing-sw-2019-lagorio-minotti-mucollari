package it.polimi.se2019.model.messages.timer;

import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.model.messages.MessageType;

public class TimerMessage extends Message {

    private TimerMessageType type;
    private TimerType timerType;
    private long time;

    public TimerMessage(TimerMessageType type, TimerType timerType, long time) {
        setMessageType(MessageType.TIMER_MESSAGE);
        this.type = type;
        this.timerType = timerType;
        this.time = time;
    }

    public TimerMessage(TimerMessageType type, TimerType timerType) {
        setMessageType(MessageType.TIMER_MESSAGE);
        this.type = type;
        this.timerType = timerType;
    }

    public TimerMessageType getType() {
        return this.type;
    }

    public TimerType getTimerType() {
        return this.timerType;
    }

    public long getTime() {
        return this.time;
    }
}
