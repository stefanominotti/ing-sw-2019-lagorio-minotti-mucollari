package it.polimi.se2019.server;

import it.polimi.se2019.model.messages.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketServer {

    private static int PORT = 9000;
    private Server server;

    SocketServer(Server server) throws IOException {
        this.server = server;
        (new ClientHandler(this, PORT)).start();
    }

    void addClient(VirtualClientInterface client) throws IOException {
        if (this.server.getClientsNumber() == 5 && this.server.canAccept()) {
            ((SocketVirtualClient) client).getSocket().close();
            client.exit();
        } else {
            this.server.addClient(client);
        }
    }

    void notifyDisconnection(VirtualClientInterface client) {
        this.server.notifyDisconnection(client);
    }

    public void send(Message message, VirtualClientInterface client) throws IOException {
        SocketVirtualClient socketClient = (SocketVirtualClient) client;
        Socket socket = socketClient.getSocket();
        ObjectOutputStream writer;

        writer = new ObjectOutputStream(socket.getOutputStream());
        writer.writeObject(message);
        writer.flush();
    }

    void notify(Message message) {
        this.server.receiveMessage(message);
    }

}
