package it.polimi.se2019.model.messages.timer;

import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.model.messages.MessageType;

/**
 * Class for handling the timer
 */
public class TimerMessage extends Message {

    private TimerMessageType type;
    private TimerType timerType;
    private long time;

    /**
     * Class constructor, it builds a timer message with the remaining time
     * @param type of the timer message
     * @param timerType of the timer
     * @param time left
     */
    public TimerMessage(TimerMessageType type, TimerType timerType, long time) {
        setMessageType(MessageType.TIMER_MESSAGE);
        this.type = type;
        this.timerType = timerType;
        this.time = time;
    }

    /**
     * Class constructor, it builds a timer message
     * @param type of the timer message
     * @param timerType of the timer
     */
    public TimerMessage(TimerMessageType type, TimerType timerType) {
        setMessageType(MessageType.TIMER_MESSAGE);
        this.type = type;
        this.timerType = timerType;
    }

    /**
     * Gets the type of the timer message
     * @return type of the message
     */
    public TimerMessageType getType() {
        return this.type;
    }

    /**
     * Gets the type of the timer
     * @return type of the timer
     */
    public TimerType getTimerType() {
        return this.timerType;
    }

    /**
     * Gets the time left for the timer
     * @return time left for the timer
     */
    public long getTime() {
        return this.time;
    }
}
