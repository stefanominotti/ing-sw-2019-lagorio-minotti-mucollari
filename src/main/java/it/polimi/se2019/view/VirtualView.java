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
                case "SkullsSettedMessage":
                    updateOne((SingleReceiverMessage) message);
                    break;
                case "ArenaCreatedMessage":
                    updateAll((Message) message);
                    break;
                case "GameSetupInterruptedMessage":
                    update((GameSetupInterruptedMessage) message);
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

    public void update(MovePlayerMessage message) {}

    public void update(IncrementScoreMessage message) {}

    public void update(GivePowerupMessage message) {}

    public void update(RemovePowerupMessage message) {}

    public void update(GiveWeaponMessage message) {}

    public void update(RemoveWeaponMessage message) {}

    public void update(FillStoreMessage message) {}

    public void update(DamageMessage message) {}

    public void update(MarkMessage message) {}

    public void update(AddKillshotMessage message) {}

    public void update(ResetPlayerBoardMessage message) {}

    public void update(GiveAmmoMessage message) {}

    public void update(RemoveAmmoMessage message) {}

    public void update(AddAmmoTileMessage message) {}

    public void update(RemoveAmmoTileMessage message) {}
}
