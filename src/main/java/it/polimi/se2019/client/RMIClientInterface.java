package it.polimi.se2019.client;

import it.polimi.se2019.model.messages.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIClientInterface extends Remote {

    void notify(Message message) throws RemoteException;

    boolean ping() throws RemoteException;

}
