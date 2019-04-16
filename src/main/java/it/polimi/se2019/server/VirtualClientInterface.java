package it.polimi.se2019.server;

import it.polimi.se2019.model.messages.Message;

import java.io.IOException;

public interface VirtualClientInterface {

     void send(Message message) throws IOException;

     void exit();
}
