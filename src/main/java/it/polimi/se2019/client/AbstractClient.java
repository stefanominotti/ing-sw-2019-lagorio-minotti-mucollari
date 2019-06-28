package it.polimi.se2019.client;

import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.view.View;

/**
 * Abstract class for handling abstract client
 */
public abstract class AbstractClient {

    private View view;

    /**
     * Class constructor, it builds an abstract client
     * @param view you want to pass to the client
     */
    AbstractClient(View view) {
        this.view = view;
    }

    /**
     * Sends a message to the abstract client
     * @param message you want to send
     */
    public abstract void send(Message message);

    /**
     * Notifies a message to the client
     * @param message you want to notify
     */
    public abstract void notify(Message message);

    /**
     * Gets the view of the abstract client
     * @return the view of the abstract client
     */
    View getView() {
        return this.view;
    }

}