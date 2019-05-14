package it.polimi.se2019.view;

import it.polimi.se2019.model.messages.*;
import it.polimi.se2019.server.Server;

import java.rmi.RemoteException;
import java.util.Observable;
import java.util.Observer;

public class VirtualView extends Observable implements Observer {

    private Server server;

    public VirtualView(Server server) {
        this.server = server;
    }

    public void forwardMessage(Message message) {
        setChanged();
        notifyObservers(message);
    }

    @Override
    public void update(Observable model, Object message) {
        String messageType = ((Message) message).getMessageType().getName()
                .replace("it.polimi.se2019.model.messages.", "");
        try {
            switch (messageType) {
                case "PlayerCreatedMessage":
                    updateOne((SingleReceiverMessage) message);
                    break;
                case "NicknameDuplicatedMessage":
                    updateOne((SingleReceiverMessage) message);
                    break;
                case "GameAlreadyStartedMessage":
                    update((GameAlreadyStartedMessage) message);
                    break;
                case "ClientDisconnectedMessage":
                    update((ClientDisconnectedMessage) message);
                    break;
                case "StartGameSetupMessage":
                    update((StartGameSetupMessage) message);
                    break;
                case "SkullsSetMessage":
                    updateOne((SingleReceiverMessage) message);
                    break;
                case "GameSetMessage":
                    updateAll((Message) message);
                    break;
                case "GameSetupInterruptedMessage":
                    update((GameSetupInterruptedMessage) message);
                    break;
                case "PowerupDrawnMessage":
                    update((PowerupDrawnMessage) message);
                    break;
                default:
                    updateAll((Message) message);
                    break;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void updateAll(Message message) throws RemoteException {
        this.server.sendAll(message);
    }

    private void updateOne(SingleReceiverMessage message) throws RemoteException {
        this.server.send(message.getCharacter(), (Message) message);
    }

    private void update(ClientDisconnectedMessage message) throws RemoteException {
        this.server.removeClient(message.getCharacter());
        this.server.setConnectionAllowed(message.isReconnectionAllowed());
        updateAll(message);
    }

    private void update(GameAlreadyStartedMessage message) throws RemoteException {
        this.server.removeClient(message.getCharacter(), message);
    }

    private void update(StartGameSetupMessage message) throws RemoteException {
        this.server.setConnectionAllowed(false);
        updateAll(message);
    }

    private void update(GameSetupInterruptedMessage message) throws RemoteException {
        this.server.setConnectionAllowed(true);
        updateAll(message);
    }

    private void update(PowerupDrawnMessage message) throws RemoteException {
        if(message.getPowerup() == null) {
            this.server.sendOthers(message.getPlayer(), message);
        } else {
            this.server.send(message.getPlayer(), message);
        }
    }

    public void send(SingleReceiverMessage message) throws RemoteException {
        this.server.send(message.getCharacter(), (Message) message);
    }

    public void sendAll(Message message) throws RemoteException {
        this.server.sendAll(message);
    }
}
