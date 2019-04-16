package it.polimi.se2019.server;

import it.polimi.se2019.client.RMIClientInterface;
import it.polimi.se2019.model.messages.Message;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class RMIProtocolServer extends UnicastRemoteObject implements RMIServerInterface {

    private static final int PORT = 1099;
    private transient Server server;

    RMIProtocolServer(Server server) throws RemoteException, MalformedURLException {
        this.server = server;

        try {
            LocateRegistry.createRegistry(PORT);
        } catch (RemoteException e) {
            System.out.println("Registry already present");
        }

        try {
            Naming.rebind("//localhost/MyServer", this);
        } catch (MalformedURLException e) {
            System.err.println("Can't register given object");
        } catch (RemoteException e) {
            System.err.println("Client error");
        }
    }

    public void addClient(RMIClientInterface client) throws RemoteException {
        RMIVirtualClient virtualClient = new RMIVirtualClient(client, this);
        if (this.server.getClientsNumber() == 5 && this.server.canAccept()) {
            throw new IllegalStateException("Full lobby");
        } else {
            this.server.addClient(virtualClient);
        }
    }

    void notifyDisconnection(VirtualClientInterface client) {
        this.server.notifyDisconnection(client);
    }

    public void send(Message message, RMIVirtualClient client) throws RemoteException {
        client.send(message);
    }

    public void notify(Message message, RMIClientInterface client) {
        server.receiveMessage(message);
    }
}
