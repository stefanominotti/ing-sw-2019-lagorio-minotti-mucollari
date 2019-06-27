package it.polimi.se2019.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ClientHandler extends Thread {

    private static final Logger LOGGER = Logger.getLogger(ClientHandler.class.getName());

    private final SocketServer server;
    private ServerSocket serverSocket;

    ClientHandler(SocketServer server, int port) {
        this.server = server;
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
           LOGGER.log(Level.SEVERE, "Error on creating Server Socket");
        }
    }

    void stopServer() {
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            // Ignore
        }
    }

    boolean isClosed() {
        return this.serverSocket.isClosed();
    }

    @Override
    public void run() {
        boolean active = true;
        while(active) {
            Socket newClientConnection;
            try {
                newClientConnection = this.serverSocket.accept();
                this.server.addClient(new SocketVirtualClient(newClientConnection, this.server));
            } catch (SocketException e) {
                active = false;
                // Ignore
            } catch (IOException e) {
                active = false;
                LOGGER.log(Level.SEVERE, "Error on adding new SocketVirtualClient to Server");
            }
        }
    }
}
