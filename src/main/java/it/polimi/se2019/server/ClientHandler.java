package it.polimi.se2019.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientHandler extends Thread {

    private final SocketServer server;
    private ServerSocket serverSocket;


    public ClientHandler(SocketServer server, int port) throws IOException {
        this.server = server;
        this.serverSocket = new ServerSocket(port);
    }

    @Override
    public void run() {
        while(true) {
            Socket newClientConnection;
            try {
                newClientConnection = this.serverSocket.accept();
                this.server.addClient(new SocketVirtualClient(newClientConnection, this.server));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
