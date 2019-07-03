package it.polimi.se2019.server;

import it.polimi.se2019.client.RMIClientInterface;
import it.polimi.se2019.model.messages.Message;

import java.rmi.RemoteException;

/**
 * Class for handling RMI virtual client
 */
public class RMIVirtualClient extends Thread implements VirtualClientInterface {

    private final RMIClientInterface client;
    private RMIProtocolServer server;
    private boolean active;

    /**
     * Class constructor, it builds a RMI virtual client
     * @param client the RMI client interface
     * @param server of which the virtual client has to be added
     */
    RMIVirtualClient(RMIClientInterface client, RMIProtocolServer server) {
        this.active = true;
        this.client = client;
        this.server = server;
    }

    /**
     * Sends a message
     * @param message to be sent
     */
    @Override
    public void send(Message message) {
        try {
            this.client.notify(message);
        } catch (RemoteException e) {
            // Ignore, ping method recognize client disconnection
        }
    }

    /**
     * Runs the RMI virtual client
     */
    @Override
    public void run() {
        while(this.active) {

            if (System.currentTimeMillis() - this.pingTime > 5000) {
                this.server.notifyDisconnection(this);
            }
            
            try {
                this.client.ping();
            } catch (RemoteException e) {
                this.server.notifyDisconnection(this);
            }

            try {
                sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Ends the application
     */
    @Override
    public void exit() {
        this.active = false;
    }
}
