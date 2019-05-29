package it.polimi.se2019.view;

import it.polimi.se2019.model.GameCharacter;
import it.polimi.se2019.model.messages.*;
import it.polimi.se2019.model.messages.board.BoardMessage;
import it.polimi.se2019.model.messages.board.BoardMessageType;
import it.polimi.se2019.model.messages.client.ClientDisconnectedMessage;
import it.polimi.se2019.model.messages.client.ClientMessage;
import it.polimi.se2019.model.messages.payment.PaymentMessage;
import it.polimi.se2019.model.messages.payment.PaymentMessageType;
import it.polimi.se2019.model.messages.player.PlayerCreatedMessage;
import it.polimi.se2019.model.messages.player.PlayerMessage;
import it.polimi.se2019.model.messages.player.PlayerMessageType;
import it.polimi.se2019.model.messages.player.PlayerReadyMessage;
import it.polimi.se2019.model.messages.powerups.PowerupMessage;
import it.polimi.se2019.model.messages.turn.TurnMessage;
import it.polimi.se2019.model.messages.turn.TurnMessageType;
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
        try {
            switch (((Message) message).getMessageType()) {
                case PLAYER_MESSAGE:
                    update((PlayerMessage) message);
                    break;
                case CLIENT_MESSAGE:
                    update((ClientMessage) message);
                    break;
                case BOARD_MESSAGE:
                    update((BoardMessage) message);
                    break;
                case POWERUP_MESSAGE:
                    update((PowerupMessage) message);
                    break;
                case SELECTION_SENT_MESSAGE:
                    send((SingleReceiverMessage) message);
                    break;
                case TURN_MESSAGE:
                    update((TurnMessage) message);
                    break;
                case PAYMENT_MESSAGE:
                    update((PaymentMessage) message);
                    break;
                default:
                    sendAll((Message) message);
                    break;
            }
        } catch (RemoteException e) {
            LOGGER.log(Level.SEVERE, "Error on sending message:", e);
        }
    }

    private void update(BoardMessage message) throws RemoteException {
        if (message.getType() == BoardMessageType.SETUP_INTERRUPTED) {
            this.server.setConnectionAllowed(true);
        }
        sendAll(message);
    }

    private void update(PaymentMessage message) throws RemoteException {
        if (message.getType() == PaymentMessageType.REQUEST) {
            send(message);
        } else {
            sendAll(message);
        }
    }

    private void update(PlayerMessage message) throws RemoteException {
        if (message.getType() == PlayerMessageType.CREATED) {
            send(message);
            sendOthers(message.getCharacter(),
                    new PlayerReadyMessage(message.getCharacter(), ((PlayerCreatedMessage) message).getNickname()));
        } else if (message.getType() == PlayerMessageType.SKULLS_SET) {
            send(message);
        } else if (message.getType() == PlayerMessageType.START_SETUP) {
            this.server.setConnectionAllowed(false);
            sendAll(message);
        } else {
            sendAll(message);
        }
    }

    private void update(ClientMessage message) throws RemoteException {
        switch (message.getType()) {
            case DISCONNECTED:
                this.server.removeClient(message.getCharacter());
                this.server.setConnectionAllowed(((ClientDisconnectedMessage) message).isReconnectionAllowed());
                sendAll(message);
                break;
            case GAME_ALREADY_STARTED:
                this.server.removeTemporaryClients(message);
                break;
            case LOAD_VIEW:
                send(message);
                break;
        }
    }

    private void update(PowerupMessage message) throws RemoteException {
        if(message.getPowerup() == null) {
            sendOthers(message.getCharacter(), message);
        } else {
            send(message);
        }
    }

    private void update(TurnMessage message) throws RemoteException {
        if (message.getType() == TurnMessageType.END) {
            this.server.saveGame();
        }
        this.server.sendAll(message);
    }

    public void send(SingleReceiverMessage message) throws RemoteException {
        this.server.send(message.getCharacter(), (Message) message);
    }

    public void sendAll(Message message) throws RemoteException {
        this.server.sendAll(message);
    }

    public void sendOthers(GameCharacter character, Message message) throws RemoteException {
        this.server.sendOthers(character, message);
    }
}
