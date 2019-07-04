package it.polimi.se2019.client;

import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.model.messages.MessageType;
import it.polimi.se2019.model.messages.client.ClientMessage;
import it.polimi.se2019.model.messages.client.ClientMessageType;
import it.polimi.se2019.view.View;

import java.io.*;
import java.net.Socket;

/**
 * Class for handling socket client, it handles thread to send message (Socket)
 */
public class SocketClient extends AbstractClient implements Runnable {

    private Socket socket;
    private long lastMessageTime;

    /**
     * Class constructor, it builds a socket client
     * @param view you want to pass
     * @param ip of the server
     * @param port of the server for socket service
     */
    public SocketClient(View view, String ip, int port) {
        super(view);
        try {
            this.socket = new Socket(ip, port);
        } catch (IOException e) {
            getView().handleConnectionError();
        }
    }

    /**
     * Send a message to server
     * @param message you want to send
     */
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

    /**
     * Run the thread to handle client input
     */
    @Override
    public void run() {
        this.lastMessageTime = System.currentTimeMillis();
        new Thread(() -> {
            while(!this.socket.isClosed()) {
                if (System.currentTimeMillis() - this.lastMessageTime > 20000) {
                    getView().handleConnectionError();
                }
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();

        try {
            while (!this.socket.isClosed()) {
                ObjectInputStream inputStream = null;
                try {
                    inputStream = new ObjectInputStream((this.socket.getInputStream()));
                } catch (IOException e) {
                    getView().handleConnectionError();
                }
                if (inputStream == null) {
                    getView().handleConnectionError();
                    break;
                }
                try {
                    Message message = (Message) inputStream.readObject();
                    if (message != null) {
                        this.lastMessageTime = System.currentTimeMillis();
                        if (message.getMessageType() == MessageType.CLIENT_MESSAGE && ((ClientMessage) message).getType()
                                == ClientMessageType.PING) {
                            send(message);
                        } else {
                            notify(message);
                        }
                    } else {
                        getView().handleConnectionError();
                    }
                } catch (IOException | ClassNotFoundException | NullPointerException e) {
                    getView().handleConnectionError();
                }
            }
        } catch (NullPointerException e) {
            // Ignore
        }
    }

    /**
     * Notifies a message to the client
     * @param message you want to notify
     */
    @Override
    public void notify(Message message) {
        getView().manageUpdate(message);
    }
}
