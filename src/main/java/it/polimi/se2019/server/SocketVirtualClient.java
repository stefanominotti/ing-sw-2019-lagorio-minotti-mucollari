package it.polimi.se2019.server;

import it.polimi.se2019.model.messages.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketVirtualClient extends Thread implements VirtualClientInterface {

    private static final Logger LOGGER = Logger.getLogger(SocketVirtualClient.class.getName());

    private Socket socket;
    private SocketServer server;
    private boolean active;

    SocketVirtualClient(Socket socket, SocketServer server) {
        this.active = true;
        this.server = server;
        this.socket = socket;
    }

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

    @Override
    public void sendClose(Message message) {
        send(message);
        exit();
    }

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

    @Override
    public void exit() {
        this.active = false;
    }

    public void closeConnection() throws IOException {
        this.socket.close();
    }
}
