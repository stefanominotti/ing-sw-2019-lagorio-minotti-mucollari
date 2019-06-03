package it.polimi.se2019.client;

import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.server.RMIServerInterface;
import it.polimi.se2019.view.View;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RMIProtocolClient extends AbstractClient implements RMIClientInterface {

    private static final Logger LOGGER = Logger.getLogger(SocketClient.class.getName());

    private RMIServerInterface server;

    public RMIProtocolClient(View view) {
        super(view);
        try {
            this.server = (RMIServerInterface) Naming.lookup("//localhost/MyServer");
            RMIClientInterface remoteRef = (RMIClientInterface) UnicastRemoteObject.exportObject(this, 0);
            this.server.addClient(remoteRef);
        } catch (MalformedURLException e) {
            LOGGER.log(Level.SEVERE, "URL not found");
        } catch (RemoteException e) {
            LOGGER.log(Level.SEVERE, "Connection error");
        } catch (NotBoundException e) {
            LOGGER.log(Level.SEVERE, "Server error");
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
