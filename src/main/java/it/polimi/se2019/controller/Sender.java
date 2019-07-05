package it.polimi.se2019.controller;

import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.model.messages.SingleReceiverMessage;
import it.polimi.se2019.view.VirtualView;

/**
 * Class for handling Sender, it is used to send messages from controller to view
 */
public class Sender {

    private VirtualView view;
    private boolean active;

    /**
     * Sender of messages
     * @param view where the messages are directed to
     */
    public Sender(VirtualView view) {
        this.view = view;
        if (view != null) {
            this.active = true;
        }
    }

    /**
     * Sends a message
     * @param message which has to be sent
     * @param toAll true if has to be sent on broadcast, else false
     */
    public void send(Object message, boolean toAll) {
        if (!this.active) {
            return;
        }
        if (toAll) {
            this.view.sendAll((Message) message);
        } else {
            this.view.send((SingleReceiverMessage) message);
        }
    }
}
