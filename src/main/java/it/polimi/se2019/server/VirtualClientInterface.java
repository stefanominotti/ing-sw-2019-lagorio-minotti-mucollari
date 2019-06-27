package it.polimi.se2019.server;

import it.polimi.se2019.model.messages.Message;

/**
 * Class for handling virtual client interface
 */
public interface VirtualClientInterface {

     /**
      * Sends a message
      * @param message to be sent
      */
     void send(Message message);

     /**
      * Ends the application
      */
     void exit();
}
