package it.polimi.se2019.server;

import it.polimi.se2019.client.RMIClientInterface;
import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.model.messages.client.ClientMessage;
import it.polimi.se2019.model.messages.client.ClientMessageType;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class RMIProtocolServer extends UnicastRemoteObject implements RMIServerInterface {

    private final static int PORT = 1099;
    private transient Server server;
    private Map<RMIClientInterface, RMIVirtualClient> clientCorrespondency;

    RMIProtocolServer(Server server) throws RemoteException {
        this.server = server;
        this.clientCorrespondency = new HashMap<>();

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

    @Override
    public void addClient(RMIClientInterface client) throws RemoteException {
        RMIVirtualClient virtualClient = new RMIVirtualClient(client, this);
        if (!this.server.isConnectionAllowed()) {
            client.notify(new ClientMessage(ClientMessageType.GAME_ALREADY_STARTED, null));
            return;
        }
        if (this.server.getClientsNumber() == 5) {
            client.notify(new ClientMessage(ClientMessageType.LOBBY_FULL));
        } else {
            this.clientCorrespondency.put(client, virtualClient);
            this.server.addClient(virtualClient);
        }
    }

    void removeClient(RMIVirtualClient client) {
        this.server.removeClient(client);
    }

    void notifyDisconnection(RMIVirtualClient client) {
        this.server.notifyDisconnection(client);
    }

    @Override
    public void notify(Message message, RMIClientInterface client) throws RemoteException {
        this.server.receiveMessage(message, this.clientCorrespondency.get(client));
    }
}
