package it.polimi.se2019.client;

import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.server.RMIServerInterface;
import it.polimi.se2019.view.CLIView;
import it.polimi.se2019.view.View;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class RMIProtocolClient implements RMIClientInterface, ClientInterface {

    private RMIServerInterface server;
    private View view;

    public RMIProtocolClient() {
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
            this.server = (RMIServerInterface) Naming.lookup("//localhost/MyServer");
            RMIClientInterface remoteRef = (RMIClientInterface) UnicastRemoteObject.exportObject(this, 0);
            this.server.addClient(remoteRef);
        } catch (MalformedURLException e) {
            System.err.println("URL non trovato!");
        } catch (RemoteException e) {
            System.err.println("Errore di connessione: " + e.getMessage() + "!");
        } catch (NotBoundException e) {
            System.err.println("Il riferimento passato non è associato a nulla!");
        }
    }

    public void send(Message message) throws RemoteException {
        this.server.notify(message, this);
    }

    public void notify(Message message) throws RemoteException {
        view.manageUpdate(message);
    }

    public boolean ping() {
        return true;
    }
}
