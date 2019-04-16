package it.polimi.se2019.server;

import it.polimi.se2019.client.RMIClientInterface;
import it.polimi.se2019.model.messages.Message;

import java.rmi.RemoteException;

public class RMIVirtualClient extends Thread implements VirtualClientInterface {

    private final RMIClientInterface client;
    private RMIProtocolServer server;
    private boolean active;

    RMIVirtualClient(RMIClientInterface client, RMIProtocolServer server) {
        this.active = true;
        this.client = client;
        this.server = server;
        this.start();
    }

    public void send(Message message) throws RemoteException {
        this.client.notify(message);
    }

    @Override
    public void run() {
        while (this.active) {
            try {
                this.client.ping();
            } catch (RemoteException e) {
                this.server.notifyDisconnection(this);
            }

            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void exit() {
        this.active = false;
    }
}
