package it.polimi.se2019.client;

import it.polimi.se2019.model.messages.Message;

import java.io.IOException;
import java.rmi.RemoteException;

public interface ClientInterface {

    void send(Message message) throws IOException;

    void notify(Message message) throws RemoteException;

}
