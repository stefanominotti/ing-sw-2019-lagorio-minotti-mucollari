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

    public SocketClient() throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("[1] - CLI");
        System.out.println("[2] - GUI");
        String viewSelection = scanner.nextLine();

        if (viewSelection.equals("1")) {
            this.view = new CLIView(this);
        } else {
            System.exit(0);
            this.view = new GUIView(this);
        }

        this.socket = new Socket(HOST, PORT);
        this.start();


    }

    public void notify(Message message) throws RemoteException {
        try {
            this.view.manageUpdate(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void send(Message message) throws IOException {
        ObjectOutputStream writer = new ObjectOutputStream(this.socket.getOutputStream());
        writer.writeObject(message);
        writer.flush();
    }

    @Override
    public void run() {
        while (!this.socket.isClosed()) {
            ObjectInputStream inputStream = null;
            try {
                inputStream = new ObjectInputStream((this.socket.getInputStream()));
            } catch (IOException e) {
                this.view.showMessage("Connection refused");
                System.exit(0);
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
