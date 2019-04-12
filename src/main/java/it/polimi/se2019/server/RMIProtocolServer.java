package it.polimi.se2019.server;

import it.polimi.se2019.client.RMIClientInterface;
import it.polimi.se2019.model.messages.Message;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class RMIProtocolServer extends UnicastRemoteObject implements RMIServerInterface {

    private static int PORT = 1099;
    private Server server;

    public RMIProtocolServer(Server server) throws RemoteException {
        this.server = server;

        try {
            LocateRegistry.createRegistry(PORT);
        } catch (RemoteException e) {
            System.out.println("Registry già presente!");
        }

        try {
            Naming.rebind("//localhost/MyServer", this);
        } catch (MalformedURLException e) {
            System.err.println("Impossibile registrare l'oggetto indicato!");
        } catch (RemoteException e) {
            System.err.println("Errore di connessione: " + e.getMessage() + "!");
        }
    }

    public synchronized void addClient(RMIClientInterface client) throws RemoteException {
        RMIVirtualClient virtualClient = new RMIVirtualClient(client, this);
        if (this.server.getClientsNumber() == 5) {
            throw new IllegalStateException("Full lobby");
        } else {
            this.server.addClient(virtualClient);
        }
    }

    public synchronized void removeClient(VirtualClientInterface client) {
        this.server.removeClient(client);
    }

    public void send(Message message, RMIVirtualClient client) throws RemoteException {
        client.send(message);
    }

    public void notify(Message message, RMIClientInterface client) {
        server.receiveMessage(message);
    }
}
