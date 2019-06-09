package it.polimi.se2019.server;

import it.polimi.se2019.client.RMIClientInterface;
import it.polimi.se2019.model.messages.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIServerInterface extends Remote {

    void notify(Message message, RMIClientInterface client) throws RemoteException;

    void addClient(RMIClientInterface client) throws RemoteException;

    void ping() throws RemoteException;
}
