package it.polimi.se2019.client;

import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.view.View;

import java.io.*;
import java.net.Socket;

public class SocketClient extends AbstractClient implements Runnable {

    private Socket socket;

    public SocketClient(View view, String ip, int port) {
        super(view);
        try {
            this.socket = new Socket(ip, port);
        } catch (IOException e) {
            getView().handleConnectionError();
        }
    }

    @Override
    public void send(Message message) {
        try {
            ObjectOutputStream writer = new ObjectOutputStream(this.socket.getOutputStream());
            writer.writeObject(message);
            writer.flush();
        } catch (IOException e) {
            getView().handleConnectionError();
        }
    }

    @Override
    public void run() {
        try {
            while (!this.socket.isClosed()) {
                ObjectInputStream inputStream = null;
                try {
                    inputStream = new ObjectInputStream((this.socket.getInputStream()));
                } catch (IOException e) {
                    getView().handleConnectionError();
                }
                try {
                    Message message = (Message) inputStream.readObject();
                    if (message != null) {
                        notify(message);
                    } else {
                        getView().handleConnectionError();
                    }
                } catch (IOException | ClassNotFoundException e) {
                    getView().handleConnectionError();
                }
            }
        } catch (NullPointerException e) {
            // Ignore
        }
    }

    @Override
    public void notify(Message message) {
       getView().manageUpdate(message);
    }
}
