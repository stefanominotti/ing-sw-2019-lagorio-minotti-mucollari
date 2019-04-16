package it.polimi.se2019.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("[1] - Socket");
        System.out.println("[2] - RMI");
        String connectionSelection = scanner.nextLine();

        if (connectionSelection.equals("1")) {
            try {
                new SocketClient();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                new RMIProtocolClient();
            } catch (IllegalStateException e) {
                System.exit(0);
            } catch (RemoteException | NotBoundException | MalformedURLException e) {
                e.printStackTrace();
            }
        }

    }
}
