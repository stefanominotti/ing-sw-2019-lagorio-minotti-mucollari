package it.polimi.se2019.client;

import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.server.RMIServerInterface;
import it.polimi.se2019.view.View;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.lang.Thread.sleep;

public class RMIProtocolClient extends AbstractClient implements RMIClientInterface {

    private RMIServerInterface server;
    private final ConcurrentLinkedQueue<Message> queue;

    public RMIProtocolClient(View view) {
        super(view);

        this.queue = new ConcurrentLinkedQueue<>();

        new Thread(() -> {
            while(true) {
                if (!RMIProtocolClient.this.queue.isEmpty()) {
                    getView().manageUpdate(RMIProtocolClient.this.queue.poll());
                }
            }
        }).start();

        try {
            this.server = (RMIServerInterface) Naming.lookup("//localhost/MyServer");
            RMIClientInterface remoteRef = (RMIClientInterface) UnicastRemoteObject.exportObject(this, 0);
            this.server.addClient(remoteRef);
        } catch (MalformedURLException | RemoteException | NotBoundException e) {
            getView().handleConnectionError();
        }

        new Thread(() -> {
            while(true) {
                try {
                    this.server.ping();
                } catch (RemoteException e) {
                    getView().handleConnectionError();;
                }

                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    @Override
    public void send(Message message) {
        try {
            this.server.notify(message, this);
        } catch (RemoteException e) {
            getView().handleConnectionError();
        }
    }

    @Override
    public void ping() {
        // Used to check if client is still connected
    }

    @Override
    public void notify(Message message) {
        this.queue.add(message);
    }
}
