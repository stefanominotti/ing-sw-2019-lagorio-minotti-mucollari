package it.polimi.se2019.server;

import it.polimi.se2019.client.RMIClientInterface;
import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.model.messages.MessageType;
import it.polimi.se2019.model.messages.client.ClientMessage;
import it.polimi.se2019.model.messages.client.ClientMessageType;

import java.rmi.RemoteException;

/**
 * Class for handling RMI virtual client
 */
public class RMIVirtualClient extends Thread implements VirtualClientInterface {

    private final RMIClientInterface client;
    private RMIProtocolServer server;
    private boolean active;
    private boolean pingActive;
    private long lastMessageTime;

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
        if (message.getMessageType() != MessageType.CLIENT_MESSAGE || ((ClientMessage) message).getType()
                != null) {
            this.pingActive = false;
        }
        try {
            this.client.notify(message);
        } catch (RemoteException e) {
            // Ignore, ping method recognize client disconnection
        }
        this.pingActive = true;
    }

    /**
     * Runs the RMI virtual client and thread that handles network issues
     */
    @Override
    public void run() {
        this.pingActive = true;
        this.lastMessageTime = System.currentTimeMillis();
        new Thread(() -> {
            while(this.active) {
                if(System.currentTimeMillis() - this.lastMessageTime > 20000) {
                    this.server.notifyDisconnection(this);
                    this.pingActive = false;
                }
            }
        }).start();
        while(this.active) {
            try {
                this.client.ping();
            } catch (RemoteException e) {
                this.server.notifyDisconnection(this);
            }

            if (this.pingActive) {
                send(new ClientMessage(ClientMessageType.PING));
            }

            try {
                sleep(10000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Updates timestamp of last message received
     */
    void updateLastMessageTime() {
        this.lastMessageTime = System.currentTimeMillis();
    }

    /**
     * Ends the application
     */
    @Override
    public void exit() {
        this.active = false;
    }
}
