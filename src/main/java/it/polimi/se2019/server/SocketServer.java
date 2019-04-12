package it.polimi.se2019.server;

import it.polimi.se2019.model.messages.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketServer {

    private static int PORT = 9000;
    private Server server;

    public SocketServer(Server server) {
        this.server = server;
        (new ClientHandler(this, PORT)).start();
    }

    public synchronized void addClient(VirtualClientInterface client) throws IOException {
        if (this.server.getClientsNumber() == 5) {
            ((SocketVirtualClient) client).getSocket().close();
            client.exit();
        } else {
            this.server.addClient(client);
        }
    }

    public synchronized void removeClient(VirtualClientInterface client) {
        this.server.removeClient(client);
    }

    public void send(Message message, VirtualClientInterface client) {
        SocketVirtualClient socketClient = (SocketVirtualClient) client;
        Socket socket = socketClient.getSocket();
        ObjectOutputStream writer;

        try {
            writer = new ObjectOutputStream(socket.getOutputStream());
            writer.writeObject(message);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void notify(Message message) {
        this.server.receiveMessage(message);
    }

}
