package it.polimi.se2019.client;

import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.server.RMIServerInterface;
import it.polimi.se2019.view.View;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIProtocolClient extends AbstractClient implements RMIClientInterface {

    private RMIServerInterface server;

    public RMIProtocolClient(View view) {
        super(view);
        try {
            this.server = (RMIServerInterface) Naming.lookup("//localhost/MyServer");
            RMIClientInterface remoteRef = (RMIClientInterface) UnicastRemoteObject.exportObject(this, 0);
            this.server.addClient(remoteRef);
        } catch (MalformedURLException | RemoteException | NotBoundException e) {
            getView().handleConnectionError();
        }
    }

    @Override
    public void send(Message message) {
        try {
            this.server.notify(message, this);
        } catch (RemoteException e) {
            getView().handleConnectionError();
        }
    }

    @Override
    public void ping() {
        // Used to check if client is still connected
    }
}
