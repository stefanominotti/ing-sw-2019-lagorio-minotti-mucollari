package it.polimi.se2019.view;

import it.polimi.se2019.client.AbstractClient;

import java.util.Scanner;

public class CLIView extends View {

    public CLIView(AbstractClient client) {
        super(client);
    }

    public void showMessage(String message) {
        System.out.println("\n" + message);
    }

    public String receiveTextInput() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }
}
