package it.polimi.se2019.client;

import java.util.Scanner;

public class Client {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("[1] - Socket");
        System.out.println("[2] - RMI");
        String connectionSelection = scanner.nextLine();

        if (connectionSelection.equals("1")) {
            Runnable r = new SocketClient();
            (new Thread(r)).start();
        } else {
            try {
                new RMIProtocolClient();
            } catch (IllegalStateException e) {
                System.exit(0);
            }
        }

    }
}
