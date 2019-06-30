package it.polimi.se2019.server;

import it.polimi.se2019.model.messages.SingleReceiverMessage;
import it.polimi.se2019.view.VirtualView;

import java.util.Observable;
import java.util.Observer;

public class ServerSingleSender extends Observable implements Observer {

    private VirtualView view;

    public ServerSingleSender(VirtualView view) {
        this.view = view;
    }

    @Override
    public void update(Observable model, Object message) {
        this.view.send((SingleReceiverMessage) message);
    }

    /**
     * Notifies changes to the Observers
     * @param object to notify
     */
    private void notifyChanges(Object object) {
        setChanged();
        notifyObservers(object);
    }

    public void send(Object message) {
        notifyChanges(message);
    }
}
