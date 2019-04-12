package it.polimi.se2019.client;

import it.polimi.se2019.view.CLIView;
import it.polimi.se2019.view.View;

import java.rmi.RemoteException;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws RemoteException {
        ClientInterface client = null;

        Scanner scanner = new Scanner(System.in);

        System.out.println("[1] - Socket");
        System.out.println("[2] - RMI");
        String connectionSelection = scanner.nextLine();

        if (connectionSelection.equals("1")) {
            client = new SocketClient();
        } else {
            try {
                client = new RMIProtocolClient();
            } catch (IllegalStateException e) {
                System.exit(0);
            }
        }

    }
}
