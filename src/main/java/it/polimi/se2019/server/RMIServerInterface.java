package it.polimi.se2019.server;

import it.polimi.se2019.client.RMIClientInterface;
import it.polimi.se2019.client.RMIProtocolClient;
import it.polimi.se2019.model.messages.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Class for handling RMI server interface
 */
public interface RMIServerInterface extends Remote {

    /**
     * Notifies a message to client
     * @param message to be notified
     * @param client to be notified
     * @throws RemoteException if the client is not reachable
     */
    void notify(Message message, RMIClientInterface client) throws RemoteException;

    /**
     * Adds a client
     * @param client to be added to the server
     * @throws RemoteException if the client is not reachable
     */
    void addClient(RMIClientInterface client) throws RemoteException;

    /**
     * Pings the client
     * @throws RemoteException if the client is not reachable
     */
    void ping(RMIClientInterface client) throws RemoteException;
}
