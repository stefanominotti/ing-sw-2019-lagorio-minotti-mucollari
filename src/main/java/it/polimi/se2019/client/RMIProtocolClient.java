package it.polimi.se2019.client;

import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.server.RMIServerInterface;
import it.polimi.se2019.view.CLIView;
import it.polimi.se2019.view.View;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class RMIProtocolClient implements RMIClientInterface, ClientInterface {

    private RMIServerInterface server;
    private View view;

    public RMIProtocolClient() throws RemoteException, MalformedURLException, NotBoundException {
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

        this.server = (RMIServerInterface) Naming.lookup("//localhost/MyServer");
        RMIClientInterface remoteRef = (RMIClientInterface) UnicastRemoteObject.exportObject(this, 0);
        this.server.addClient(remoteRef);

    }

    public void send(Message message) throws RemoteException {
        this.server.notify(message, this);
    }

    public void notify(Message message) throws RemoteException {
        try {
            view.manageUpdate(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ping() {}
}
