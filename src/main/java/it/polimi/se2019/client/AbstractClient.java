package it.polimi.se2019.client;

import it.polimi.se2019.model.messages.Message;
import it.polimi.se2019.view.CLIView;
import it.polimi.se2019.view.GUIView;
import it.polimi.se2019.view.View;

import java.rmi.RemoteException;
import java.util.Scanner;

public abstract class AbstractClient {

    private View view;

    public AbstractClient(View view) {
        this.view = view;
    }

    public abstract void send(Message message);

    public void notify(Message message) throws RemoteException {
        this.view.manageUpdate(message);
    }

}
