package it.polimi.se2019.client;

import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.server.RMIServerInterface;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIProtocolClient extends AbstractClient implements RMIClientInterface {

    private RMIServerInterface server;

    RMIProtocolClient() {
        super();
        try {
            this.server = (RMIServerInterface) Naming.lookup("//localhost/MyServer");
            RMIClientInterface remoteRef = (RMIClientInterface) UnicastRemoteObject.exportObject(this, 0);
            this.server.addClient(remoteRef);
        } catch (MalformedURLException e) {
            showMessage("URL not found");
        } catch (RemoteException e) {
            showMessage("Connection error");
        } catch (NotBoundException e) {
            showMessage("Server error");
        }
    }

    @Override
    public void send(Message message) throws RemoteException {
        this.server.notify(message, this);
    }

    @Override
    public void ping() {
        // Used to check if client is still connected
    }
}
