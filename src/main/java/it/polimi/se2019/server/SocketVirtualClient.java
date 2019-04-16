package it.polimi.se2019.server;

import it.polimi.se2019.model.messages.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class SocketVirtualClient extends Thread implements VirtualClientInterface {

    private Socket socket;
    private SocketServer server;
    private boolean active;

    SocketVirtualClient(Socket socket, SocketServer server) {
        this.active = true;
        this.server = server;
        this.socket = socket;
        this.start();
    }

    public void send(Message message) throws IOException {
        this.server.send(message, this);
    }

    Socket getSocket() {
        return this.socket;
    }

    @Override
    public void run() {
        try {
            while (this.active && !this.socket.isClosed()) {
                ObjectInputStream inputStream = new ObjectInputStream((this.socket.getInputStream()));
                Message message = (Message) inputStream.readObject();
                if (message != null) {
                    this.server.notify(message);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            this.server.notifyDisconnection(this);
        }
    }

    public void exit() {
        this.active = false;
    }

}
