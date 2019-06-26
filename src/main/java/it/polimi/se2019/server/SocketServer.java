package it.polimi.se2019.server;

import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.model.messages.client.ClientMessage;
import it.polimi.se2019.model.messages.client.ClientMessageType;


class SocketServer {

    private static final int PORT = 12345;
    private Server server;
    private ClientHandler clientHandler;

    SocketServer(Server server) {
        this.server = server;
        this.clientHandler = new ClientHandler(this, PORT);
        this.clientHandler.start();
    }

    void stop() {
        this.clientHandler.stopClientHandler();
    }
    public boolean isClosed() {
        return this.clientHandler.isClosed();
    }

    void addClient(SocketVirtualClient client) {
        if (!this.server.isConnectionAllowed()) {
            client.send(new ClientMessage(ClientMessageType.GAME_ALREADY_STARTED, null));
            return;
        }
        if (this.server.getClientsNumber() < 5) {
            this.server.addClient(client);
        } else if (this.server.getClientsNumber() == 5){
            client.send(new ClientMessage(ClientMessageType.LOBBY_FULL));
        }
    }

    void notifyDisconnection(SocketVirtualClient client) {
        this.server.notifyDisconnection(client);
    }

    void notify(Message message, VirtualClientInterface client) {
        this.server.receiveMessage(message, client);
    }

}
