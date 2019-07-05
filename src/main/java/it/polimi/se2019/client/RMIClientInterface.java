package it.polimi.se2019.client;

import it.polimi.se2019.model.messages.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Class for handling RMI Client Interface
 * @author stefanominotti
 */
public interface RMIClientInterface extends Remote {

    /**
     * Notifies a message to the client
     * @param message which you want to notify
     * @throws RemoteException if the client is not reachable
     */
    void notify(Message message) throws RemoteException;

    /**
     * Pings the client
     * @throws RemoteException if the client is not reachable
     */
    void ping() throws RemoteException;
}