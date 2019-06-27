package it.polimi.se2019.server;

import it.polimi.se2019.client.RMIClientInterface;
import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.model.messages.client.ClientMessage;
import it.polimi.se2019.model.messages.client.ClientMessageType;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RMIProtocolServer extends UnicastRemoteObject implements RMIServerInterface {

    private static final Logger LOGGER = Logger.getLogger(RMIProtocolServer.class.getName());

    private transient Server server;
    private Map<RMIClientInterface, RMIVirtualClient> clientCorrespondency;
    private final ConcurrentLinkedQueue<ClientMessagePair> queue;
    private Registry registry;

    RMIProtocolServer(Server server, int port) throws RemoteException {
        this.server = server;
        this.clientCorrespondency = new HashMap<>();

        this.queue = new ConcurrentLinkedQueue<>();

        new Thread(() -> {
            while(true) {
                if (!RMIProtocolServer.this.queue.isEmpty()) {
                    ClientMessagePair toReceive = RMIProtocolServer.this.queue.poll();
                    RMIProtocolServer.this.server.receiveMessage(toReceive.getMessage(), toReceive.getClient());
                }
            }
        }).start();

        try {
            this.registry = LocateRegistry.createRegistry(port);
        } catch (RemoteException e) {
            LOGGER.log(Level.SEVERE, "Registry already present");
        }

        try {
            Naming.rebind("//localhost/MyServer", this);
        } catch (MalformedURLException e) {
            LOGGER.log(Level.SEVERE, "Can't register given object");
        } catch (RemoteException e) {
            LOGGER.log(Level.SEVERE, "Client error");
        }
    }

    void stopServer() {
        try {
            UnicastRemoteObject.unexportObject(this.registry,true);
        } catch (NoSuchObjectException e) {
            // Ignore
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

    void notifyDisconnection(RMIVirtualClient client) {
        this.server.notifyDisconnection(client);
    }

    @Override
    public void notify(Message message, RMIClientInterface client) {
        this.queue.add(new ClientMessagePair(this.clientCorrespondency.get(client), message));
    }

    @Override
    public void ping() {
        // Used to check if server is still connected
    }
}
