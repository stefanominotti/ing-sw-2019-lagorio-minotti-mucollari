package it.polimi.se2019.server;

import it.polimi.se2019.model.messages.Message;

import java.io.IOException;

class SocketServer {

    private static int PORT = 9000;
    private Server server;

    SocketServer(Server server) {
        this.server = server;
        (new ClientHandler(this, PORT)).start();
    }

    void addClient(SocketVirtualClient client) throws IOException {
        if (this.server.getClientsNumber() < 5) {
            this.server.addClient(client);
        }
    }

    void removeClient(SocketVirtualClient client) {
        this.server.removeClient(client);
    }

    void notifyDisconnection(SocketVirtualClient client) {
        this.server.notifyDisconnection(client);
    }

    void notify(Message message) {
        this.server.receiveMessage(message);
    }

}
