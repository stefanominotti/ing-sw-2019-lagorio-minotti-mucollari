package it.polimi.se2019.controller;

import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.model.messages.SingleReceiverMessage;
import it.polimi.se2019.view.VirtualView;

import java.util.Observable;
import java.util.Observer;

public class Sender {

    private VirtualView view;
    private boolean active;

    public Sender(VirtualView view) {
        this.view = view;
        if (view != null) {
            this.active = true;
        }
    }

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
