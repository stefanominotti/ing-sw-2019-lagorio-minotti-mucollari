package it.polimi.se2019.client;

import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.view.View;

import java.rmi.RemoteException;

public interface ClientInterface {

    void send(Message message) throws RemoteException;

    void notify(Message message) throws RemoteException;

}
