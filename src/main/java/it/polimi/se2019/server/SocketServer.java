package it.polimi.se2019.server;

import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.model.messages.client.ClientMessage;
import it.polimi.se2019.model.messages.client.ClientMessageType;

/**
 * Class for handling socket server
 * @author stefanominotti
 */
class SocketServer {

    private Server server;
    private ClientHandler clientHandler;

    /**
     * Class constructor, it builds a socket server
     * @param server server of which you want to build a socket server
     * @param port on which the socket service has to be hosted
     */
    SocketServer(Server server, int port) {
        this.server = server;
        this.clientHandler = new ClientHandler(this, port);
        this.clientHandler.start();
    }

    /**
     * Stops the socket server
     */
    void stopServer() {
        this.clientHandler.stopServer();
    }

    /**
     * Knows if the socket is open or closed
     * @return true for open, false for closed
     */
    boolean isClosed() {
        return this.clientHandler.isClosed();
    }

    /**
     * Adds a virtual client to the server
     * @param client to be added
     */
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

    /**
     * Notifies a disconnection
     * @param client disconnected
     */
    void notifyDisconnection(SocketVirtualClient client) {
        this.server.notifyDisconnection(client);
    }

    /**
     * Notifies a message
     * @param message need to be notified
     * @param client which has to be notified
     */
    void notify(Message message, VirtualClientInterface client) {
        this.server.receiveMessage(message, client);
    }

}
