package it.polimi.se2019.view;

import it.polimi.se2019.model.messages.*;
import it.polimi.se2019.server.Server;

import java.rmi.RemoteException;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VirtualView extends Observable implements Observer {

    private static final Logger LOGGER = Logger.getLogger(VirtualView.class.getName());

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
                    update((PlayerCreatedMessage) message);
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
                case "GameSetupInterruptedMessage":
                    update((GameSetupInterruptedMessage) message);
                    break;
                case "PowerupDrawnMessage":
                    update((PowerupDrawnMessage) message);
                    break;
                case "AvailableActionsMessage":
                    updateOne((SingleReceiverMessage) message);
                    break;
                case "AvailableMoveActionMessage":
                    updateOne((SingleReceiverMessage) message);
                    break;
                case "AvailablePickupActionMessage":
                    updateOne((SingleReceiverMessage) message);
                    break;
                case "WeaponPickupSelectionMessage":
                    updateOne((SingleReceiverMessage) message);
                    break;
                case "RequireWeaponSwitchMessage":
                    updateOne((SingleReceiverMessage) message);
                    break;
                case "RequireWeaponLoadMessage":
                    updateOne((SingleReceiverMessage) message);
                    break;
                default:
                    updateAll((Message) message);
                    break;
            }
        } catch (RemoteException e) {
            LOGGER.log(Level.SEVERE, "Error on sending message:", e);
        }
    }

    private void updateAll(Message message) throws RemoteException {
        this.server.sendAll(message);
    }

    private void updateOne(SingleReceiverMessage message) throws RemoteException {
        this.server.send(message.getCharacter(), (Message) message);
    }

    private void update(PlayerCreatedMessage message) throws RemoteException {
        this.server.send(message.getCharacter(), message);
        this.server.sendOthers(message.getCharacter(),
                new PlayerReadyMessage(message.getCharacter(), message.getNickname()));
    }

    private void update(ClientDisconnectedMessage message) throws RemoteException {
        this.server.removeClient(message.getCharacter());
        this.server.setConnectionAllowed(message.isReconnectionAllowed());
        updateAll(message);
    }

    private void update(GameAlreadyStartedMessage message) throws RemoteException {
        this.server.removeTemporaryClients(message);
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
