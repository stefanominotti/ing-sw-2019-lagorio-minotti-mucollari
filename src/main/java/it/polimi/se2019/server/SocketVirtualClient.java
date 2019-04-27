package it.polimi.se2019.server;

import it.polimi.se2019.model.messages.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;

public class SocketVirtualClient extends Thread implements VirtualClientInterface {

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
            e.printStackTrace();
        }
    }

    @Override
    public void sendClose(Message message) throws RemoteException {
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
                    this.server.notify(message);
                }
            }
        } catch (IOException e) {
            this.server.notifyDisconnection(this);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
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
