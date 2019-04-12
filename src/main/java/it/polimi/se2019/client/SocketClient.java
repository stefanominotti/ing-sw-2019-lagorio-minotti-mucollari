package it.polimi.se2019.client;

import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.view.CLIView;
import it.polimi.se2019.view.View;

import java.io.*;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.Scanner;

public class SocketClient extends Thread implements ClientInterface {

    private static final int PORT = 9000;
    private static final String HOST = "localhost";

    private View view;
    private Socket socket;

    public SocketClient() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("[1] - CLI");
        System.out.println("[2] - GUI");
        String viewSelection = scanner.nextLine();

        if (viewSelection.equals("1")) {
            this.view = new CLIView(this);
        } else {
            this.view = new GUIView(this);
        }

        try {
            this.socket = new Socket(HOST, PORT);
            this.start();

        } catch (IOException e) {
            System.out.println("Connection Error.");
            e.printStackTrace();
        }

    }

    public void notify(Message message) throws RemoteException {
        this.view.manageUpdate(message);
    }

    public synchronized void send(Message message) {

        try {
            ObjectOutputStream writer = new ObjectOutputStream(this.socket.getOutputStream());
            writer.writeObject(message);
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void stopConnection () {

        if (!socket.isClosed()) {
            try {
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Connection closed.");
        }
    }

    @Override
    public void run() {
        while (!this.socket.isClosed()) {
            ObjectInputStream inputStream = null;
            try {
                inputStream = new ObjectInputStream((this.socket.getInputStream()));
            } catch (EOFException e) {
                this.view.showMessage("Connection refused");
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Message message = (Message) inputStream.readObject();
                if (message != null) {
                    notify(message);
                } else {
                    System.exit(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
