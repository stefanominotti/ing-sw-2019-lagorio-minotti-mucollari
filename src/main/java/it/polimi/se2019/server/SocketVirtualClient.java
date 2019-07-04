package it.polimi.se2019.server;

import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.model.messages.MessageType;
import it.polimi.se2019.model.messages.client.ClientMessage;
import it.polimi.se2019.model.messages.client.ClientMessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for handling socket virtual client
 */
public class SocketVirtualClient extends Thread implements VirtualClientInterface {

    private static final Logger LOGGER = Logger.getLogger(SocketVirtualClient.class.getName());

    private Socket socket;
    private SocketServer server;
    private boolean active;
    private boolean pingActive;
    private long lastMessageTime;

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
        if (message.getMessageType() == MessageType.CLIENT_MESSAGE && ((ClientMessage) message).getType()
                == null) {
            this.pingActive = false;
        }
        try {
            writer = new ObjectOutputStream(this.socket.getOutputStream());
            writer.writeObject(message);
            writer.flush();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error on sending Message");
        }
        this.pingActive = true;
    }


    /**
     * Runs the socket virtual client
     */
    @Override
    public void run() {
        this.pingActive = true;
        this.lastMessageTime = System.currentTimeMillis();
        new Thread(() -> {
            while(this.active) {
                if (this.pingActive) {
                    send(new ClientMessage(ClientMessageType.PING));
                }
                if (System.currentTimeMillis() - this.lastMessageTime > 20000) {
                    this.server.notifyDisconnection(this);
                    this.pingActive = false;
                }
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();

        try {
            while (this.active && !this.socket.isClosed()) {
                ObjectInputStream inputStream = new ObjectInputStream((this.socket.getInputStream()));
                Message message = (Message) inputStream.readObject();
                if (message != null) {
                    this.lastMessageTime = System.currentTimeMillis();
                    if (message.getMessageType() != MessageType.CLIENT_MESSAGE || ((ClientMessage) message).getType()
                            != ClientMessageType.PING) {
                        this.server.notify(message, this);
                    }
                }
            }
        } catch (IOException e) {
            this.server.notifyDisconnection(this);
            this.pingActive = false;
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error on managing Stream");
        }

        if(!this.socket.isClosed()) {
            try {
                this.socket.close();
            } catch (IOException e) {
                // Ignore
            }
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
