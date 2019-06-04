package it.polimi.se2019.server;

import it.polimi.se2019.model.messages.Message;

import java.rmi.RemoteException;

public interface VirtualClientInterface {

     void send(Message message);

     void sendClose(Message message);

     void exit();
}
