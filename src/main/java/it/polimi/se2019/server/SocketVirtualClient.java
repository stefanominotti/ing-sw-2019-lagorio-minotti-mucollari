package it.polimi.se2019.server;

import it.polimi.se2019.model.messages.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for handling socket virtual client
 * @author stefanominotti
 */
public class SocketVirtualClient extends Thread implements VirtualClientInterface {

    private static final Logger LOGGER = Logger.getLogger(SocketVirtualClient.class.getName());

    private Socket socket;
    private SocketServer server;
    private boolean active;

    /**
     * Class constructor, it builds a socket virtual client
     * @param socket of which the virtual client has to be paired
     * @param server of which the virtual client has to be added
     */
    SocketVirtualClient(Socket socket, SocketServer server) {
        this.active = true;
        this.server = server;
        this.socket = socket;
    }

    /**
     * Sends a message
     * @param message to be sent
     */
    @Override
    public void send(Message message) {
        ObjectOutputStream writer;

        try {
            writer = new ObjectOutputStream(this.socket.getOutputStream());
            writer.writeObject(message);
            writer.flush();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error on sending Message");
        }
    }


    /**
     * Runs the socket virtual client
     */
    @Override
    public void run() {
        try {
            while (this.active && !this.socket.isClosed()) {
                ObjectInputStream inputStream = new ObjectInputStream((this.socket.getInputStream()));
                Message message = (Message) inputStream.readObject();
                if (message != null) {
                    this.server.notify(message, this);
                }
            }
        } catch (IOException e) {
            this.server.notifyDisconnection(this);
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error on managing Stream");
        }
        if(!this.socket.isClosed()) {
            this.socket.isClosed();
        }
    }

    /**
     * Ends the application
     */
    @Override
    public void exit() {
        this.active = false;
    }

}
