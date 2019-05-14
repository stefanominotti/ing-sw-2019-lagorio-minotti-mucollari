package it.polimi.se2019.view;

import it.polimi.se2019.client.AbstractClient;
import it.polimi.se2019.server.ClientHandler;

import java.rmi.RemoteException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CLIView extends View {

    private static final Logger LOGGER = Logger.getLogger(ClientHandler.class.getName());

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
                        LOGGER.log(Level.SEVERE, "Error on executing remote method call:" + e.toString(), e);
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
