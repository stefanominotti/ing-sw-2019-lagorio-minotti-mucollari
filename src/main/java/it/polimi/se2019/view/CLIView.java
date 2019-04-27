package it.polimi.se2019.view;

import it.polimi.se2019.client.AbstractClient;

import java.rmi.RemoteException;
import java.util.Scanner;

public class CLIView extends View {

    private Thread inputThread;

    public CLIView(AbstractClient client) {
        super(client);

        this.inputThread = new Thread() {
            Scanner scanner = new Scanner(System.in);
            boolean read = true;
            @Override
            public void run() {
                while (this.read) {
                    String input = this.scanner.nextLine();
                    try {
                        handleInput(input);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        this.inputThread.start();
    }

    public void showMessage(String message) {
        System.out.println("\n" + message);
    }
}
