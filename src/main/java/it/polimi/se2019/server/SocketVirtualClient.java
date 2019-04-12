package it.polimi.se2019.server;

import it.polimi.se2019.model.messages.Message;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;

public class SocketVirtualClient extends Thread implements VirtualClientInterface {

    private Socket socket;
    private SocketServer server;
    private boolean active;

    public SocketVirtualClient(Socket socket, SocketServer server) {
        this.active = true;
        this.server = server;
        this.socket = socket;
        this.start();
    }

    public void send(Message message) {
        this.server.send(message, this);
    }

    public Socket getSocket() {
        return this.socket;
    }

    public void run() {
        try {
            while (this.active && !this.socket.isClosed()) {

                ObjectInputStream inputStream = new ObjectInputStream((this.socket.getInputStream()));
                Message message = (Message) inputStream.readObject();
                if (message != null) {
                    this.server.notify(message);
                } else {
                    this.socket.close();
                    this.server.removeClient(this);
                    break;
                }
            }
        } catch (EOFException e) {
            // Ignore
        } catch (SocketException e) {
            exit();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void exit() {
        this.active = false;
    }

}
