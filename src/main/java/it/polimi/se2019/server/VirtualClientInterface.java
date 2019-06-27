package it.polimi.se2019.server;

import it.polimi.se2019.model.messages.Message;

public interface VirtualClientInterface {

     void send(Message message);

     void exit();
}
