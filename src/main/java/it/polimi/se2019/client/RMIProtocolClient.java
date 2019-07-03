package it.polimi.se2019.client;

import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.server.RMIServerInterface;
import it.polimi.se2019.view.View;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;

import static java.lang.Thread.sleep;

/**
 * Class for handling RMI protocol client, it handles thread to empty RMI messages queue and send message (RMI)
 */
public class RMIProtocolClient extends AbstractClient implements RMIClientInterface {

    private RMIServerInterface server;
    private final LinkedList<Message> queue;
    private long pingTime;

    /**
     * Class constructor, it builds an RMI protocol client
     * @param view which you want to pass
     * @param ip of the server
     */
    public RMIProtocolClient(View view, String ip) {
        super(view);

        this.queue = new LinkedList<>();

        new Thread(() -> {
            synchronized (RMIProtocolClient.this.queue) {
                while (true) {
                    if (!RMIProtocolClient.this.queue.isEmpty()) {
                        Message message = RMIProtocolClient.this.queue.poll();
                        if (message != null) {
                            getView().manageUpdate(message);
                        }
                    } else {
                        try {
                            RMIProtocolClient.this.queue.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
        }).start();

        try {
            this.server = (RMIServerInterface) Naming.lookup("//" + ip + "/MyServer");
            RMIClientInterface remoteRef = (RMIClientInterface) UnicastRemoteObject.exportObject(this, 0);
            this.server.addClient(remoteRef);
        } catch (MalformedURLException | RemoteException | NotBoundException e) {
            getView().handleConnectionError();
        }

        new Thread(() -> {
            while(true) {
                if (System.currentTimeMillis() - this.pingTime > 5000) {
                    getView().handleConnectionError();
                }

                try {
                    this.server.ping(this);
                } catch (RemoteException e) {
                    getView().handleConnectionError();
                }

                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    /**
     * Sends a message to the client
     * @param message you want to send
     */
    @Override
    public void send(Message message) {
        try {
            this.server.notify(message, this);
        } catch (RemoteException e) {
            getView().handleConnectionError();
        }
    }

    /**
     * Pings the client
     */
    @Override
    public void ping() {
        this.pingTime = System.currentTimeMillis();
        // Used to check if client is still connected
    }

    /**
     * Notifies a message to the client adding it to the RMI messages queue
     * @param message you want to notify
     */
    @Override
    public void notify(Message message) {
        synchronized (this.queue) {
            this.queue.add(message);
            this.queue.notifyAll();
        }
    }
}
