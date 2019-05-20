package it.polimi.se2019.server;

import it.polimi.se2019.model.messages.GameAlreadyStartedMessage;
import it.polimi.se2019.model.messages.LobbyFullMessage;
import it.polimi.se2019.model.messages.Message;

import java.io.IOException;
import java.rmi.RemoteException;

class SocketServer {

    private static int PORT = 12345;
    private Server server;

    SocketServer(Server server) {
        this.server = server;
        (new ClientHandler(this, PORT)).start();
    }

    void addClient(SocketVirtualClient client) throws IOException {
        if (!this.server.isConnectionAllowed()) {
            client.send(new GameAlreadyStartedMessage());
            return;
        }
        if (this.server.getClientsNumber() < 5) {
            this.server.addClient(client);
        } else if (this.server.getClientsNumber() == 5){
            client.send(new LobbyFullMessage());
        }
    }

    void removeClient(SocketVirtualClient client) {
        this.server.removeClient(client);
    }

    void notifyDisconnection(SocketVirtualClient client) {
        this.server.notifyDisconnection(client);
    }

    void notify(Message message, VirtualClientInterface client) {
        try {
            this.server.receiveMessage(message, client);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
