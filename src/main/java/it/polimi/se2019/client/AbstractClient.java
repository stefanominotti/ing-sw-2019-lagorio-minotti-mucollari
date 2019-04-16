package it.polimi.se2019.client;

import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.view.CLIView;
import it.polimi.se2019.view.GUIView;
import it.polimi.se2019.view.View;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Scanner;

public abstract class AbstractClient implements ClientInterface {

    private View view;

    public AbstractClient() {
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
    }

    public abstract void send(Message message) throws RemoteException;

    public void notify(Message message) throws RemoteException {
        try {
            this.view.manageUpdate(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void showMessage(String message) {
        this.view.showMessage(message);
    }

}
