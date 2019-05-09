package it.polimi.se2019.client;

import it.polimi.se2019.model.messages.Message;

import java.io.*;
import java.net.Socket;

public class SocketClient extends AbstractClient implements Runnable {

    private static final int PORT = 12345;
    private static final String HOST = "localhost";

    private Socket socket;

    SocketClient() {
        super();
        try {
            this.socket = new Socket(HOST, PORT);
        } catch (IOException e) {
            showMessage("Connection error");
            e.printStackTrace();
        }
    }

    @Override
    public void send(Message message) {
        try {
            ObjectOutputStream writer = new ObjectOutputStream(this.socket.getOutputStream());
            writer.writeObject(message);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!this.socket.isClosed()) {
            ObjectInputStream inputStream = null;
            try {
                inputStream = new ObjectInputStream((this.socket.getInputStream()));
            } catch (EOFException e) {
                showMessage("Connection error");
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (inputStream == null) {
                showMessage("Connection error");
                System.exit(0);
            }
            try {
                Message message = (Message) inputStream.readObject();
                if (message != null) {
                    notify(message);
                } else {
                    System.exit(0);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
